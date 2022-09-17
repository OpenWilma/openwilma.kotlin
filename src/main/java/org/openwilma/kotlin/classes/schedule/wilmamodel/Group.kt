package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName

class Group(
    @field:SerializedName("Id") var id: Int, @field:SerializedName(
        "courseId"
    )
    var courseId: Int,

    @field:SerializedName("ShortCaption") var shortCaption: String, @field:SerializedName(
        "Caption"
    ) var caption: String,

    @field:SerializedName("FullCaption") var fullCaption: String, @field:SerializedName(
        "Class"
    ) var className: String,

    @field:SerializedName("Teachers") var teachers: List<Teacher>, @field:SerializedName(
        "Rooms"
    ) var rooms: List<Room>
)