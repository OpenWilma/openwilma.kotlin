package org.openwilma.kotlin.classes.courses

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.Date

data class WilmaHomework(
    val date: Date?,
    @SerializedName("caption", alternate = ["lesson", "note"])
    val homework: String?,
)
