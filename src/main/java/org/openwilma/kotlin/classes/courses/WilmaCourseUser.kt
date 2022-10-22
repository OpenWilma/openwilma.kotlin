package org.openwilma.kotlin.classes.courses

import com.google.gson.annotations.SerializedName

data class WilmaCourseUser (
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?
)
