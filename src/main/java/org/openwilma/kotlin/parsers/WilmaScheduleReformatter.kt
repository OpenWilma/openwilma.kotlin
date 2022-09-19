package org.openwilma.kotlin.parsers

import org.openwilma.kotlin.classes.responses.ScheduleResponse
import org.openwilma.kotlin.classes.schedule.models.ScheduleDay
import org.openwilma.kotlin.classes.schedule.models.WilmaSchedule
import org.openwilma.kotlin.classes.schedule.wilmamodel.Reservation
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*


class WilmaScheduleReformatter {
    companion object {

        private fun mergeTimeAndDate(date: Date, time: LocalTime): Date {
            val c = Calendar.getInstance()
            c.time = date
            c.set(Calendar.HOUR, time.hour)
            c.set(Calendar.MINUTE, time.minute)
            c.set(Calendar.SECOND, time.second)
            return c.time
        }

        fun getCorrectWeekDay(weekDay: Int): Int {
            when (weekDay) {
                1 -> return Calendar.MONDAY
                2 -> return Calendar.TUESDAY
                3 -> return Calendar.WEDNESDAY
                4 -> return Calendar.THURSDAY
                5 -> return Calendar.FRIDAY
                6 -> return Calendar.SATURDAY
                7 -> return Calendar.SUNDAY
            }
            return weekDay
        }

        fun reformatSchedule(schedule: ScheduleResponse, date: LocalDate = LocalDate.now()): WilmaSchedule {
            // Get monday date
            val c = Calendar.getInstance()
            c.time = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC))
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.HOUR, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek);

            val monday = c.time
            val reservations: LinkedHashMap<Long, MutableList<Reservation>> = linkedMapOf()

            // Making hashmap
            for (reservation in schedule.reservations) {
                val reservationDay = reservation.day

                // Setting date
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getTimeZone("Europe/Helsinki")
                calendar.time = monday
                calendar.set(Calendar.DAY_OF_WEEK, getCorrectWeekDay(reservationDay))

                val dayUnix = calendar.timeInMillis

                reservation.startDate = mergeTimeAndDate(calendar.time, LocalTime.parse(reservation.start))
                reservation.endDate = mergeTimeAndDate(calendar.time, LocalTime.parse(reservation.end))

                if (reservations.contains(dayUnix)) {
                    val existingArray: MutableList<Reservation> = reservations[dayUnix]!!
                    existingArray.add(reservation)
                    reservations.set(dayUnix, existingArray)
                } else {
                    reservations[dayUnix] = mutableListOf(reservation)
                }
            }

            // Creating final result

            val days: MutableList<ScheduleDay> = mutableListOf()
            for (key in reservations.keys) {
                days.add(ScheduleDay(reservations[key]!!, Date(key)))
            }

            //days.sortBy { it.day }
            return WilmaSchedule(days = days, terms = schedule.terms)
        }
    }
}