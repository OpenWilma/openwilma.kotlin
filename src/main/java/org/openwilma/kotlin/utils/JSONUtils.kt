package org.openwilma.kotlin.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object JSONUtils {

    fun isJSONValid(test: String): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }

}