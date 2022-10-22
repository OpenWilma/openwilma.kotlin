package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.courses.WilmaCourse
import org.openwilma.kotlin.classes.courses.WilmaCourseExam
import org.openwilma.kotlin.classes.courses.WilmaCourseUser
import org.openwilma.kotlin.classes.courses.WilmaHomework
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.exams.Exam
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.CourseTimeRange
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.URLUtils
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun getCourses(wilmaSession: WilmaSession, timeRange: CourseTimeRange): WilmaAPIResponse<List<WilmaCourse>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$timeRange"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaCourse>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}


public suspend fun getCourse(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<WilmaCourse> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<WilmaCourse>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun getCourseExams(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaCourseExam>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/exams"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaCourseExam>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun getCourseHomework(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaHomework>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/homework"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaHomework>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

/**
 * Notice! This method works with teacher roles/accounts only!
 */
public suspend fun getCourseStudents(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaCourseUser>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/students"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaCourseUser>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}