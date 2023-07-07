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

class AnnouncementsKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private var openWilma = OpenWilma()

    @BeforeEach
    internal fun setUp(): Unit = runBlocking {
        openWilma.signInToWilma(wilmaServer, "ope", "ope")
        openWilma.roles().payload?.find { it.type != UserType.WILMA_ACCOUNT }?.let { openWilma.wilmaSession.setRole(it) }
    }


    @Test
    fun testAnnouncements() = runBlocking {
        val exams = openWilma.announcements()
        println(GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }


    @Test
    fun testAnnouncement() = runBlocking {
        val announcement = openWilma.announcement(21)
        println(GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(announcement))
    }

}