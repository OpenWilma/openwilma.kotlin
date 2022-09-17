package org.openwilma.kotlin.parsers

import org.junit.jupiter.api.Test
import java.util.*

class WilmaScheduleReformatterTest {

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
}