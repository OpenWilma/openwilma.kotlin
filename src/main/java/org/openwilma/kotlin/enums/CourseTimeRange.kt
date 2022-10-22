package org.openwilma.kotlin.enums

enum class CourseTimeRange(private val value: String) {
    CURRENT(""),
    PAST("past");

    override fun toString(): String {
        return value
    }
}