package org.openwilma.kotlin.classes.schedule.models

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.classes.schedule.wilmamodel.Reservation
import java.util.Date

data class ScheduleDay(
    @field:SerializedName("reservationList") var reservations: List<Reservation>,
    @field:SerializedName("day") var day: Date
)