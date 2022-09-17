package org.openwilma.kotlin.classes.schedule.models

import org.openwilma.kotlin.classes.schedule.wilmamodel.Term

data class WilmaSchedule (val days: List<ScheduleDay>, val terms: List<Term>)