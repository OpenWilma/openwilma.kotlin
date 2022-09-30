package org.openwilma.kotlin.parsers

import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object WilmaJSONParser {
    // Custom Gson instance with custom date format and date handling
    val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm")
        .registerTypeAdapter(Date::class.java, object: JsonDeserializer<Date> {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val formatWithoutDate = SimpleDateFormat("yyyy-MM-dd")
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
            return try {
                format.parse(json?.asString)
            } catch (e: ParseException) {
                try {
                    formatWithoutDate.parse(json?.asString)
                } catch (e: ParseException) {
                    null
                }
            }
        }
    }).create()
}