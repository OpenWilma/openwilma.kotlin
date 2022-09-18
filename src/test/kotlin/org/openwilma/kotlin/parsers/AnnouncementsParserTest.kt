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

class AnnouncementsParserTest {

    @Test
    fun parseAnnouncementsList() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("announcements.html")!!.toURI()))
        val announcements = WilmaAnnouncementsParser.parseAnnouncements(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(announcements))
    }


    @Test
    fun parseAnnouncementContent() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("announcement.html")!!.toURI()))
        val announcement = WilmaAnnouncementsParser.parseAnnouncement(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(announcement))
    }

    @Test
    fun parseAnnouncementContentWithoutDescription() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("announcement_nodesc.html")!!.toURI()))
        val announcement = WilmaAnnouncementsParser.parseAnnouncement(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(announcement))
    }

}