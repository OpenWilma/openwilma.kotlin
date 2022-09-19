package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


data class Reservation  (
    @SerializedName("ReservationID")
    var reservationID: Int,
    @SerializedName("ScheduleID")
    var scheduleID: Int,
    @SerializedName("Day")
    var day: Int,
    @SerializedName("Start")
    var start: String,
    var startDate: Date,
    var endDate: Date,
    @SerializedName("End")
    var end: String,
    @SerializedName("Color")
    var color: String,
    @SerializedName("X1")
    var x1: Int,
    @SerializedName("Y1")
    var y1: Int,
    @SerializedName("X2")
    var x2: Int,
    @SerializedName("Y2")
    var y2: Int,
    @SerializedName("Class")
    var wilmaClass: String,
    @SerializedName("AllowEdit")
    var isAllowEdit: Boolean,
    @SerializedName("AllowAddMoveRemove")
    var isAllowAddMoveRemove: Boolean,
    @SerializedName("Groups")
    private var Groups: List<Group>,
    @SerializedName("Dates")
    var dates: List<Date>? = null
)
