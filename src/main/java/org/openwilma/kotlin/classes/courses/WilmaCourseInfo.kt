package org.openwilma.kotlin.classes.courses

data class WilmaCourseInfo(
    val id: Int,
    val caption: String,
    val description: String?,
    val abbreviation: String?,
    val scope: WilmaCourseScope?
)