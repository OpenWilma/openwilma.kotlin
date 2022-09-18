package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.announcements.Announcement
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.exams.Exam
import org.openwilma.kotlin.classes.responses.JSONErrorResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.parsers.WilmaAnnouncementsParser
import org.openwilma.kotlin.parsers.WilmaExamsParser
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.JSONUtils
import org.openwilma.kotlin.utils.URLUtils
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun OpenWilma.Companion.getAnnouncements(wilmaSession: WilmaSession): List<Announcement> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "news?printable&format=json"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                if (JSONUtils.isJSONValid(response)) {
                    val error: JSONErrorResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<JSONErrorResponse>() {}.type)
                    if (error.wilmaError != null) {
                        it.resumeWithException(error.wilmaError!!)
                        return
                    }
                }
                it.resume(WilmaAnnouncementsParser.parseAnnouncements(response))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun OpenWilma.Companion.getAnnouncement(wilmaSession: WilmaSession, id: Int): Announcement? {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "news/$id?printable&format=json"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                if (JSONUtils.isJSONValid(response)) {
                    val error: JSONErrorResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<JSONErrorResponse>() {}.type)
                    if (error.wilmaError != null) {
                        it.resumeWithException(error.wilmaError!!)
                        return
                    }
                }
                it.resume(WilmaAnnouncementsParser.parseAnnouncement(response))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}