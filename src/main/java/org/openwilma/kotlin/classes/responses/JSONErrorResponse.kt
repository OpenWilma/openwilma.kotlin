package org.openwilma.kotlin.classes.responses

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.classes.errors.WilmaError

open class JSONErrorResponse(@field:SerializedName("error") var wilmaError: WilmaError? = null)