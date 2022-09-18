package org.openwilma.kotlin.parsers

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

class LessonNoteParserTest {

    @Test
    fun parseLessonNotes() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes.html")!!.toURI()))
        val notes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        assert(notes.first().noteCodename == "03")
        assert(notes.first().noteName == "Muu kouluntoiminta /ei tunnilla")
        assert(notes.first().courseCode == "MU2")
        assert(notes.first().authorCodeName == "BLA")
        assert(notes.first().authorName == "Bianca Blackburn")
        assert(notes.first().backgroundColor == "#80FF80")
        assert(notes.first().foregroundColor == "#000000")
        assert(notes.first().noteStart == LocalDateTime.of(2018, 2, 27, 9, 0, 0))
        assert(notes.first().noteEnd == LocalDateTime.of(2018, 2, 27, 9, 45, 0))
        assert(notes.first().duration == 45)
        assert(notes.first().clarifiedBy == null)
        assert(notes.count() == 14)
    }

    @Test
    fun parseEmptyLessonNotes() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes_empty.html")!!.toURI()))
        val notes = WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
        assert(notes.isEmpty())
    }
}