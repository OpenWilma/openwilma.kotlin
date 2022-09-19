package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.lessonnotes.LessonNote
import org.openwilma.kotlin.classes.responses.JSONErrorResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.LessonNoteRange
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.parsers.WilmaLessonNoteParser
import org.openwilma.kotlin.utils.JSONUtils
import org.openwilma.kotlin.utils.URLUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun getLessonNotes(wilmaSession: WilmaSession, dateRange: LessonNoteRange = LessonNoteRange.DEFAULT, start: LocalDate? = null, end: LocalDate? = null): List<LessonNote> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        val dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy", Locale.getDefault())
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "attendance/view?range=${dateRange.identifier}"+(if (start != null) "&first=${start.format(dateFormat)}" else "")+(if (end != null) "&last=${end.format(dateFormat)}" else "")+"&printable&format=json"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                if (JSONUtils.isJSONValid(response)) {
                    val error: JSONErrorResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<JSONErrorResponse>() {}.type)
                    if (error.wilmaError != null) {
                        it.resumeWithException(error.wilmaError!!)
                        return
                    }
                }
                it.resume(WilmaLessonNoteParser.parseWilmaLessonNotes(response))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}