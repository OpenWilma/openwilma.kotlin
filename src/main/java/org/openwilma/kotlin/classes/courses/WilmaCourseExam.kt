package org.openwilma.kotlin.classes.courses

import java.util.*

data class WilmaCourseExam (
    val id: Int,
    val date: Date,
    val caption: String?,
    val topic: String?,
    val timeStart: Date?,
    val timeEnd: Date?,
    val grade: String?,
    val verbalGrade: String?
)