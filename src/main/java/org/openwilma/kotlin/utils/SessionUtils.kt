package org.openwilma.kotlin.utils

import com.google.gson.reflect.TypeToken
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.errors.ExpiredSessionError
import org.openwilma.kotlin.classes.responses.JSONErrorResponse
import org.openwilma.kotlin.config.Config
import org.openwilma.kotlin.parsers.WilmaJSONParser
import java.lang.Exception
import java.util.regex.Pattern

object SessionUtils {
    private val invalidSessionErrorCodes = listOf("common-20", "common-18", "common-15")

    fun parseSessionCookie(cookieValue: String?): String? {
        val sessionPattern = Pattern.compile(Config.sessionRegex)
        val valueMatcher = sessionPattern.matcher(cookieValue)
        return if (valueMatcher.find()) {
            valueMatcher.group(2)
        } else null
    }

    fun checkSessionExpiration(response: String) {
        var containsError = false
        try {
            val error = WilmaJSONParser.gson.fromJson<JSONErrorResponse>(response, object: TypeToken<JSONErrorResponse>() {}.type)
            error.wilmaError?.let {
                containsError = invalidSessionErrorCodes.contains(it.errorID)
            }
        } catch (ignored: Exception) {}
        if (containsError && OpenWilma.checkSessionErrors) {
            throw ExpiredSessionError()
        }
    }
}