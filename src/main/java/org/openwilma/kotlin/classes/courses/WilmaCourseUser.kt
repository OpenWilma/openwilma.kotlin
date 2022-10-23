package org.openwilma.kotlin.classes.courses

import com.google.gson.annotations.SerializedName

data class WilmaCourseUser (
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstname", alternate = ["firstName"])
    val firstName: String?,
    @SerializedName("lastname", alternate = ["lastName"])
    val lastName: String?
)
