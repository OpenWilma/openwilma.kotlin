package org.openwilma.kotlin

import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.announcements.Announcement
import org.openwilma.kotlin.classes.courses.*
import org.openwilma.kotlin.classes.exams.Exam
import org.openwilma.kotlin.classes.lessonnotes.LessonNote
import org.openwilma.kotlin.classes.responses.SessionResponse
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.classes.responses.WilmaServersResponse
import org.openwilma.kotlin.classes.schedule.models.WilmaSchedule
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.enums.CourseTimeRange
import org.openwilma.kotlin.enums.LessonNoteRange
import org.openwilma.kotlin.methods.*
import java.io.File
import java.time.LocalDate


public class OpenWilma {

    var wilmaSession: WilmaSession = WilmaSession()

    companion object {
        const val version = 12
        const val versionName = "0.9.16-beta"
        const val minimumSupportedWilmaVersion = 19
        const val lessonNoteFullHourWidth = 5.63
        var cacheDirectory = System.getProperty("user.home") + File.separator+ ".openwilma_cache"
        var checkSessionErrors = true
        var disableRoleRequirement = false
        var customUserAgent: String? = null

        // Announcements
        suspend fun announcements(wilmaSession: WilmaSession): List<Announcement> = getAnnouncements(wilmaSession)
        suspend fun announcement(wilmaSession: WilmaSession, id: Int): Announcement? = getAnnouncement(wilmaSession, id)

        // Authentication
        suspend fun signInToWilma(wilmaServer: WilmaServer, username: String, password: String, skipVersionValidation: Boolean = false): WilmaSession = signIn(wilmaServer, username, password, skipVersionValidation)
        suspend fun newSession(wilmaServer: WilmaServer, skipVersionValidation: Boolean = false): SessionResponse = getSessionId(wilmaServer, skipVersionValidation)

        // Exams
        suspend fun upcomingExams(wilmaSession: WilmaSession): List<Exam> = getUpcomingExams(wilmaSession)
        suspend fun pastExams(wilmaSession: WilmaSession, start: LocalDate? = null, end: LocalDate? = null): List<Exam> = getPastExams(wilmaSession, start, end)

        // Lesson Notes
        suspend fun lessonNotes(wilmaSession: WilmaSession, dateRange: LessonNoteRange = LessonNoteRange.DEFAULT, start: LocalDate? = null, end: LocalDate? = null): List<LessonNote> = getLessonNotes(wilmaSession, dateRange, start, end)

        // Profile
        suspend fun account(wilmaSession: WilmaSession): WilmaAPIResponse<WilmaAccountInfo> = getUserAccount(wilmaSession)
        suspend fun roles(wilmaSession: WilmaSession): WilmaAPIResponse<List<WilmaRole>> = getRoles(wilmaSession)
        suspend fun activeUserFormKey(wilmaSession: WilmaSession, roleRequired: Boolean = false): String? = getActiveUserFormKey(wilmaSession, roleRequired)
        suspend fun activeRole(wilmaSession: WilmaSession, roleRequired: Boolean = false): WilmaRole? = getActiveRole(wilmaSession, roleRequired)

        // Schedule
        suspend fun schedule(wilmaSession: WilmaSession, date: LocalDate = LocalDate.now()): WilmaSchedule = getSchedule(wilmaSession, date)
        suspend fun scheduleRange(wilmaSession: WilmaSession, start: LocalDate, end: LocalDate): WilmaSchedule = getScheduleRange(wilmaSession, start, end)

        // Courses

        suspend fun courses(wilmaSession: WilmaSession, timeRange: CourseTimeRange, skipAdditionalInformation: Boolean = true): WilmaAPIResponse<List<WilmaCourse>> = getCourses(wilmaSession, timeRange, skipAdditionalInformation = skipAdditionalInformation)
        suspend fun course(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<WilmaCourse> = getCourse(wilmaSession, id)
        suspend fun courseAdditionalInfo(wilmaSession: WilmaSession, courseId: Int): WilmaAPIResponse<WilmaCourseInfo> = getCourseAdditionalInformation(wilmaSession, courseId)
        suspend fun courseExams(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaCourseExam>> = getCourseExams(wilmaSession, id)
        suspend fun courseHomework(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaHomework>> = getCourseHomework(wilmaSession, id)
        suspend fun courseStudents(wilmaSession: WilmaSession, id: Int): WilmaAPIResponse<List<WilmaCourseUser>> = getCourseStudents(wilmaSession, id)

        // Wilma Servers
        suspend fun wilmaServers(): WilmaServersResponse = getWilmaServers()
    }

    suspend fun announcements(): List<Announcement> = getAnnouncements(wilmaSession)
    suspend fun announcement(id: Int): Announcement? = getAnnouncement(wilmaSession, id)
    suspend fun signInToWilma(wilmaServer: WilmaServer, username: String, password: String, skipVersionValidation: Boolean = false) {
        wilmaSession = signIn(wilmaServer, username, password, skipVersionValidation)
    }
    suspend fun newSession(wilmaServer: WilmaServer, skipVersionValidation: Boolean = false): SessionResponse = getSessionId(wilmaServer, skipVersionValidation)
    suspend fun upcomingExams(): List<Exam> = getUpcomingExams(wilmaSession)
    suspend fun pastExams(start: LocalDate? = null, end: LocalDate? = null): List<Exam> = getPastExams(wilmaSession, start, end)
    suspend fun lessonNotes(dateRange: LessonNoteRange = LessonNoteRange.DEFAULT, start: LocalDate? = null, end: LocalDate? = null): List<LessonNote> = getLessonNotes(wilmaSession, dateRange, start, end)
    suspend fun account() = getUserAccount(wilmaSession)
    suspend fun roles(): WilmaAPIResponse<List<WilmaRole>> = getRoles(wilmaSession)
    suspend fun activeUserFormKey(roleRequired: Boolean = false): String? = getActiveUserFormKey(wilmaSession, roleRequired)
    suspend fun activeRole(roleRequired: Boolean = false): WilmaRole? = getActiveRole(wilmaSession, roleRequired)
    suspend fun schedule(date: LocalDate = LocalDate.now()): WilmaSchedule = getSchedule(wilmaSession, date)
    suspend fun scheduleRange(start: LocalDate, end: LocalDate): WilmaSchedule = getScheduleRange(wilmaSession, start, end)
    suspend fun courses(timeRange: CourseTimeRange, skipAdditionalInformation: Boolean = true): WilmaAPIResponse<List<WilmaCourse>> = getCourses(wilmaSession, timeRange, skipAdditionalInformation = skipAdditionalInformation)
    suspend fun course(id: Int): WilmaAPIResponse<WilmaCourse> = getCourse(wilmaSession, id)
    suspend fun courseAdditionalInfo(courseId: Int): WilmaAPIResponse<WilmaCourseInfo> = getCourseAdditionalInformation(wilmaSession, courseId)
    suspend fun courseExams(id: Int): WilmaAPIResponse<List<WilmaCourseExam>> = getCourseExams(wilmaSession, id)
    suspend fun courseHomework(id: Int): WilmaAPIResponse<List<WilmaHomework>> = getCourseHomework(wilmaSession, id)
    suspend fun courseStudents(id: Int): WilmaAPIResponse<List<WilmaCourseUser>> = getCourseStudents(wilmaSession, id)
}