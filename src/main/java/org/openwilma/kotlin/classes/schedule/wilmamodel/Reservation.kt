package org.openwilma.kotlin.classes.schedule.wilmamodel

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.Date

class Reservation(
    @field:SerializedName("ReservationID") var reservationId: Int, @field:SerializedName(
        "ScheduleID"
    ) var scheduleId: Int, @field:SerializedName("Day") var day: Int, @field:SerializedName(
        "Start"
    ) var start: String, @field:SerializedName("End") var end: String, @field:SerializedName(
        "Color"
    ) var hexColor: String, @field:SerializedName("Class") var className: String, @field:SerializedName(
        "Groups"
    ) var groups: List<Group>,
    var startDate: Date?,
    var endDate: Date?
)