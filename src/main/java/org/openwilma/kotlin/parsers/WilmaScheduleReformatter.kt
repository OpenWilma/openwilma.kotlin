package org.openwilma.kotlin.parsers

import com.google.gson.Gson
import org.openwilma.kotlin.classes.responses.ScheduleResponse
import org.openwilma.kotlin.classes.schedule.models.ScheduleDay
import org.openwilma.kotlin.classes.schedule.models.WilmaSchedule
import org.openwilma.kotlin.classes.schedule.wilmamodel.Reservation
import java.text.SimpleDateFormat
import java.time.*
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*


class WilmaScheduleReformatter {
    companion object {

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
            val firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(WeekFields.ISO.firstDayOfWeek))
            val c = GregorianCalendar.from(firstDayOfWeek.atStartOfDay(ZoneId.systemDefault()))
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.HOUR, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)

            val monday = c.time
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val reservations: LinkedHashMap<Long, MutableList<Reservation>> = linkedMapOf()
            var currentDay: Int = (schedule.reservations.firstOrNull()?.day ?: 1)-1
            var lastDay = 0

            // Making hashmap
            for (reservation in schedule.reservations) {
                val reservationDay = reservation.day

                // handle if schedule starts after monday, record last day of schedule and keep track of current day
                if (reservationDay > lastDay) {
                    if (lastDay != 0) {
                        currentDay = reservationDay - 1
                    }
                    lastDay = reservationDay
                }

                // Setting date
                val calendar = Calendar.getInstance()
                calendar.time = monday
                calendar.add(Calendar.DAY_OF_YEAR, currentDay-1)

                val dayUnix = calendar.timeInMillis

                reservation.startDate = Date.from(LocalDateTime.of(LocalDate.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()), LocalTime.parse(reservation.start)).atZone(
                    ZoneId.systemDefault()).toInstant())
                reservation.endDate = Date.from(LocalDateTime.of(LocalDate.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()), LocalTime.parse(reservation.end)).atZone(
                    ZoneId.systemDefault()).toInstant())

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