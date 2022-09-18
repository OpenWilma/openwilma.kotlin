package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.exams.Exam
import org.openwilma.kotlin.classes.responses.JSONErrorResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.parsers.WilmaExamsParser
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.JSONUtils
import org.openwilma.kotlin.utils.URLUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public object OpenWilma {

}
public suspend fun OpenWilma.Companion.getUpcomingExams(wilmaSession: WilmaSession): List<Exam> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "exams/calendar?format=json"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                if (JSONUtils.isJSONValid(response)) {
                    val error: JSONErrorResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<JSONErrorResponse>() {}.type)
                    if (error.wilmaError != null) {
                        it.resumeWithException(error.wilmaError!!)
                        return
                    }
                }
                it.resume(WilmaExamsParser.parseUpcomingExams(response))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun OpenWilma.Companion.getPastExams(wilmaSession: WilmaSession, start: LocalDate? = null, end: LocalDate? = null): List<Exam> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        val dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy", Locale.getDefault())
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "exams/calendar/past?printable&format=json"+(if (start != null && end != null) "&range=-3&first=${dateFormat.format(start)}&last=${dateFormat.format(end)}" else "")), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                if (JSONUtils.isJSONValid(response)) {
                    val schedule: JSONErrorResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<JSONErrorResponse>() {}.type)
                    if (schedule.wilmaError != null) {
                        it.resumeWithException(schedule.wilmaError!!)
                        return
                    }
                }
                it.resume(WilmaExamsParser.parsePastExams(response))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}