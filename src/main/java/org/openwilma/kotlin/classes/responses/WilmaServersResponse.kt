package org.openwilma.kotlin.classes.responses

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.classes.WilmaServer

data class WilmaServersResponse(@field:SerializedName("wilmat") var wilmaServers: List<WilmaServer>) : JSONErrorResponse()
