package org.openwilma.kotlin.classes.responses

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.classes.errors.WilmaError

class WilmaAPIResponse<T>(
    @field:SerializedName("error") var error: WilmaError?,
    @field:SerializedName("statusCode") var statusCode: Int,
    @field:SerializedName("payload") var payload: T?
)