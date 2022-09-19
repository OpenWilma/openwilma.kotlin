package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName
import java.util.*


data class Group (
    @SerializedName("Id")
    var id: Int? = 0,
    @SerializedName("CourseId")
    var courseId: Int? = 0,
    @SerializedName("CourseName")
    var courseName: String? = null,
    @SerializedName("CourseCode")
    var courseCode: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("Caption")
    var caption: String? = null,
    @SerializedName("ShortCaption")
    var shortCaption: String? = null,
    @SerializedName("LongCaption")
    var longCaption: String? = null,
    @SerializedName("FullCaption")
    var fullCaption: String? = null,
    @SerializedName("StartDate")
    private var StartDate: Date? = null,
    @SerializedName("EndDate")
    private var EndDate: Date? = null,
    @SerializedName("Committed")
    var isCommitted: Boolean = false,
    @SerializedName("Teachers")
    var teachers: List<Teacher>? = null,
    @SerializedName("Rooms")
    var rooms: List<Room> = ArrayList()
)
