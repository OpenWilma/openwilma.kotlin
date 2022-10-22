package org.openwilma.kotlin.classes.people

import com.google.gson.annotations.SerializedName

data class WilmaTeacher (
    val primusId: Int?,
    val codeName: String,
    val fullName: String,
    val firstName: String? = null,
    val lastName: String? = null
)