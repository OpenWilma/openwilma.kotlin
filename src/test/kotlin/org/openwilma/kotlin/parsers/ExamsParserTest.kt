package org.openwilma.kotlin.parsers

import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ExamsParserTest {

    @Test
    fun parseUpcomingExams() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("exams_upcoming.data")!!.toURI()))
        val exams = WilmaExamsParser.parseUpcomingExams(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }

    @Test
    fun parseUpcomingExamsWithGrades() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("exams_upcoming_graded.data")!!.toURI()))
        val exams = WilmaExamsParser.parseUpcomingExams(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }

    @Test
    fun parsePastExams() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("exams_past.data")!!.toURI()))
        val exams = WilmaExamsParser.parsePastExams(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(exams))
    }
}