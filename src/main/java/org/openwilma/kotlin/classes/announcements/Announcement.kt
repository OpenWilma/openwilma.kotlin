package org.openwilma.kotlin.classes.announcements

import org.openwilma.kotlin.enums.UserType
import java.util.*

data class Announcement(
    val id: Int,
    val subject: String,
    val description: String?,
    val contentHTML: String?,
    val authorName: String?,
    val authorCode: String?,
    val authorId: Int?,
    val authorType: UserType?,
    val timestamp: Date?,
    val important: Boolean = false,
    val permanent: Boolean = false
)
