package org.openwilma.kotlin.utils;

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Date


class DateUtils {
    companion object {
        val wilmaFinnishDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val wilmaFinnishLocalDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        fun splitWeeksFromRange(start: LocalDate, end: LocalDate, weekFields: WeekFields): List<LocalDate> {
            val result: MutableList<LocalDate> = mutableListOf()
            var date: LocalDate = start.with(TemporalAdjusters.previousOrSame(weekFields.firstDayOfWeek))
            while (date <= end) {
                val weekStart: LocalDate = date
                date = date.plusDays(7)
                result.add(weekStart)
            }
            return result
        }
    }
}
