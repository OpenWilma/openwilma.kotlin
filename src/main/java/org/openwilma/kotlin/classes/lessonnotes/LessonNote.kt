package org.openwilma.kotlin.classes.lessonnotes

import java.time.LocalDateTime

data class LessonNote(
    val id: Long?,
    var clarificationId: Int?,
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
    val clarifiedBy: String?,
    var needsClarification: Boolean = false
)
