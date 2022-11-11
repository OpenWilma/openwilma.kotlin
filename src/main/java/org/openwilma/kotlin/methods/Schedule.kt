package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.responses.ScheduleResponse
import org.openwilma.kotlin.classes.schedule.models.ScheduleDay
import org.openwilma.kotlin.classes.schedule.models.WilmaSchedule
import org.openwilma.kotlin.classes.schedule.wilmamodel.Term
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.parsers.WilmaScheduleReformatter
import org.openwilma.kotlin.utils.DateUtils
import org.openwilma.kotlin.utils.DateUtils.Companion.wilmaFinnishLocalDateFormat
import org.openwilma.kotlin.utils.URLUtils
import java.time.LocalDate
import java.time.temporal.WeekFields
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun getSchedule(wilmaSession: WilmaSession, date: LocalDate = LocalDate.now()): WilmaSchedule {
    // Role required for user type
    val currentRole: WilmaRole = wilmaSession.getRole() ?: getActiveRole(wilmaSession, false)!!
    return suspendCoroutine {
        val httpClient = WilmaHttpClient.getInstance()
        // Exception for user type guardian: needs to be set as student
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "schedule/export/${if (currentRole.type == UserType.GUARDIAN) UserType.STUDENT.userTypeString else currentRole.type.userTypeString}s/${currentRole.primusId}/index_json?date=${wilmaFinnishLocalDateFormat.format(date)}"), wilmaSession, object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                val schedule: ScheduleResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<ScheduleResponse>() {}.type)
                if (schedule.wilmaError != null) {
                    it.resumeWithException(schedule.wilmaError!!)
                    return
                }
                it.resume(WilmaScheduleReformatter.reformatSchedule(schedule, date))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun getScheduleRange(wilmaSession: WilmaSession, start: LocalDate, end: LocalDate): WilmaSchedule {
    val days: MutableList<ScheduleDay> = mutableListOf()
    val terms: MutableList<Term> = mutableListOf()
    for (week in DateUtils.splitWeeksFromRange(start, end, WeekFields.ISO)) {
        val schedule = getSchedule(wilmaSession, week)
        days.addAll(schedule.days)
        if (schedule.terms.isNotEmpty() && terms.isEmpty()) {
            terms.clear()
            terms.addAll(schedule.terms)
        }
    }
    return WilmaSchedule(days, terms)
}