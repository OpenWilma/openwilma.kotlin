package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName
import java.util.*

class Term(
    @field:SerializedName("Name") var name: String, @field:SerializedName(
        "StartDate"
    ) var start: Date, @field:SerializedName("EndDate") var end: Date
)