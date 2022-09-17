package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName

class Teacher(
    @field:SerializedName("Id") var id: Int, @field:SerializedName(
        "Caption"
    ) var codeName: String, @field:SerializedName("LongCaption") var name: String, @field:SerializedName(
        "ScheduleVisible"
    ) var isScheduleVisible: Boolean
)