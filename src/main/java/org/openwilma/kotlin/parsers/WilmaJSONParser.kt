package org.openwilma.kotlin.parsers

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object WilmaJSONParser {
    // Custom Gson instance with custom date format
    val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create()
}