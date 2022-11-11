package org.openwilma.kotlin.methods

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.enums.CourseTimeRange
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CoursesKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private var client = OpenWilma()

    @BeforeEach
    internal fun setUp(): Unit = runBlocking {
        client.signInToWilma(wilmaServer, "oppilas", "oppilas")
    }


    @Test
    fun testCourses() = runBlocking {
        val courses = client.courses(timeRange = CourseTimeRange.CURRENT)
        println("Get extra!")
        val coursesWithExtraDetails = client.courses(timeRange = CourseTimeRange.CURRENT, skipAdditionalInformation = false)
        println("Got extra!")
        client.courses(timeRange = CourseTimeRange.CURRENT, skipAdditionalInformation = false)
        println("Got it again for cache validation!")
        val coursesPast = client.courses(timeRange = CourseTimeRange.PAST)
        val course = client.course(21128)
        val courseExams = client.courseExams(21128)
        val courseHomework = client.courseHomework(21128)
        val courseStudents = client.courseStudents(21128)
        println(GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(listOf(coursesPast, coursesWithExtraDetails, courses, course, courseExams, courseHomework, courseStudents)))
    }
}