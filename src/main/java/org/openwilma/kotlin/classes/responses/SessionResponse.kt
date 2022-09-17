package org.openwilma.kotlin.classes.responses

import com.google.gson.annotations.SerializedName

class SessionResponse(
    @field:SerializedName("SessionID") var sessionId: String, @field:SerializedName(
        "ApiVersion"
    ) var apiVersion: Int
) : JSONErrorResponse()