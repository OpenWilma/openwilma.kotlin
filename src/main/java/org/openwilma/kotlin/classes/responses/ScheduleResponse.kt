package org.openwilma.kotlin.classes.responses

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.classes.schedule.wilmamodel.Reservation
import org.openwilma.kotlin.classes.schedule.wilmamodel.Term

class ScheduleResponse(
    @field:SerializedName("Schedule") var reservations: List<Reservation>, @field:SerializedName(
        "Terms"
    ) var terms: List<Term>
): JSONErrorResponse()