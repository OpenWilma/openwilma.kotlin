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

class LessonNoteParserTest {

    @Test
    fun parseLessonNotes() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes.data")!!.toURI()))
        val notes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        assert(notes.first().noteCodename == "03")
        assert(notes.first().noteName == "Muu kouluntoiminta /ei tunnilla")
        assert(notes.first().courseCode == "MU2")
        assert(notes.first().authorCodeName == "BLA")
        assert(notes.first().authorName == "Bianca Blackburn")
        assert(notes.first().backgroundColor == "#80ff80")
        assert(notes.first().foregroundColor == "#000000" || notes.first().foregroundColor == "#000")
        assert(notes.first().noteStart == LocalDateTime.of(2018, 2, 27, 9, 0, 0))
        assert(notes.first().noteEnd == LocalDateTime.of(2018, 2, 27, 9, 45, 0))
        assert(notes.first().duration == 45)
        assert(notes.first().clarifiedBy == null)
        assert(notes.count() == 14)
    }

    @Test
    fun parseEmptyLessonNotes() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes_empty.data")!!.toURI()))
        val notes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        assert(notes.isEmpty())
    }

    @Test
    fun testParseClarificationInfo() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes_clarify.data")!!.toURI()))
        val notes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        println(
            GsonBuilder()
                .setPrettyPrinting()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(notes))
    }

    @Test
    fun testParseClarificationCombineInfo() {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create()
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes_clarify.data")!!.toURI()))
        val htmlDoc2 = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes_clarify_all.data")!!.toURI()))
        val clarifyingNotes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        val allNotes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc2)
        assert(!allNotes[0].needsClarification)
        assert(allNotes[0].clarificationId == null)
        clarifyingNotes.filter { note -> note.needsClarification }.forEach {note ->
            val index = allNotes.indexOfFirst { aNote -> aNote.id == note.id }
            if (index != -1) {
                allNotes[index].clarificationId = note.clarificationId
                allNotes[index].needsClarification = note.needsClarification
            }
        }
        println(
            gson.toJson(allNotes))
        assert(clarifyingNotes[0].needsClarification)
        assert(clarifyingNotes[0].clarificationId == 91900579)
        assert(allNotes[0].needsClarification)
        assert(allNotes[0].clarificationId == 91900579)
    }
}