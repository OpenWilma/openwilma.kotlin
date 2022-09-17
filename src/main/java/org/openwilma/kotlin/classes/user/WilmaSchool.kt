package org.openwilma.kotlin.classes.user

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.enums.UserType
import java.util.StringJoiner

data class WilmaSchool(
    @field:SerializedName("id") var id: Int,
    @field:SerializedName("caption") var name: String,
    @field:SerializedName("features") var features: List<String> = arrayListOf(),
)