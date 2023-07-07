package org.openwilma.kotlin.parsers

import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.utils.DateUtils
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.WeekFields
import java.util.*

class WilmaScheduleReformatterTest {

    companion object {
        val gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create()
    }


    @Test
    fun testWeekDayConverter() {
        assert(WilmaScheduleReformatter.getCorrectWeekDay(1) == Calendar.MONDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(2) == Calendar.TUESDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(3) == Calendar.WEDNESDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(4) == Calendar.THURSDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(5) == Calendar.FRIDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(6) == Calendar.SATURDAY)
        assert(WilmaScheduleReformatter.getCorrectWeekDay(7) == Calendar.SUNDAY)
    }

    @Test
    fun testWeekSplitter() {
        val list = DateUtils.splitWeeksFromRange(LocalDate.now(), LocalDate.now().plusDays(12), WeekFields.ISO)
        println(
            gson.toJson(list))
        println(gson.toJson(arrayOf(LocalDate.now(), LocalDate.now().plusDays(12))))
    }
}