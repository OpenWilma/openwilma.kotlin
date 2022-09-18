package org.openwilma.kotlin.classes.lessonnotes

import java.time.LocalDateTime
import java.util.StringJoiner

data class LessonNote(
    val noteCodename: String?,
    val noteName: String?,
    val discName: String?,
    val courseCode: String?,
    val courseName: String?,
    val authorCodeName: String?,
    val authorName: String?,
    val additionalInfo: String?,
    val backgroundColor: String?,
    val foregroundColor: String?,
    val noteStart: LocalDateTime?,
    val noteEnd: LocalDateTime?,
    val duration: Int?,
    val clarifiedBy: String?
)
