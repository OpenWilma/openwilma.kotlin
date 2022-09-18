package org.openwilma.kotlin.methods

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.enums.LessonNoteRange
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ExamsKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private lateinit var session: WilmaSession

    @BeforeEach
    internal fun setUp() = runBlocking {
        session = OpenWilma.signIn(wilmaServer, "oppilas", "oppilas")
    }


    @Test
    fun testPastExams() = runBlocking {
        val exams = OpenWilma.getPastExams(wilmaSession = session, start = LocalDate.of(2016, 12, 9), end = LocalDate.of(2023, 6, 3))
        println("past: "+GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }

    @Test
    fun testUpcomingExams() = runBlocking {
        val exams = OpenWilma.getUpcomingExams(wilmaSession = session)
        println("upcoming: "+GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }
}