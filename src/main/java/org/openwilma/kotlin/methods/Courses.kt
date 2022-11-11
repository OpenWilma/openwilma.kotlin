package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.courses.*
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.CourseTimeRange
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.URLUtils
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun getCourses(wilmaSession: WilmaSession, timeRange: CourseTimeRange, skipAdditionalInformation: Boolean = true): WilmaAPIResponse<List<WilmaCourse>> {
    val courses: WilmaAPIResponse<List<WilmaCourse>> = suspendCoroutine {
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$timeRange"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaCourse>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
    if (!skipAdditionalInformation) {
        val coursesWithAdditionalInfo: List<WilmaCourse>? = courses.payload?.map { course ->
            course.courseId?.let {
                val info = getCourseAdditionalInformation(wilmaSession, it);
                course.additionalInfo = info.payload
            }
            course
        }
        courses.payload = coursesWithAdditionalInfo
    }
    return courses
}

public suspend fun getCourseAdditionalInformation(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<WilmaCourseInfo> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getCachedRequest(URLUtils.buildUrl(wilmaSession, "api/v1/courses/$id"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<WilmaCourseInfo>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}


public suspend fun getCourse(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<WilmaCourse> {
    val course: WilmaAPIResponse<WilmaCourse> =  suspendCoroutine {
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<WilmaCourse>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
    course.payload?.courseId?.let {
        val info = getCourseAdditionalInformation(wilmaSession, it);
        course.payload?.additionalInfo = info.payload
    }
    return course
}

public suspend fun getCourseExams(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaCourseExam>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/exams"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
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
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/homework"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
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
        val httpClient = WilmaHttpClient.getInstance()
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/gradebooks/$id/students"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
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