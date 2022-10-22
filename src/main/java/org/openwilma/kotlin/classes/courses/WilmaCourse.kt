package org.openwilma.kotlin.classes.courses

import java.time.LocalDate
import java.util.*

data class WilmaCourse (
    val id: Int,
    val caption: String?,
    val courseId: Int?,
    val name: String?,
    val startDate: Date?,
    val endDate: Date?,
    val committed: Boolean,
    val teachers: List<WilmaCourseUser>?
)