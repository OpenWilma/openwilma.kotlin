package org.openwilma.kotlin.parsers

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class LessonNoteParserTest {

    @Test
    fun parseLessonNotes() {
        val htmlDoc = Files.readString(Paths.get(javaClass.classLoader.getResource("lesson_notes.html")!!.toURI()))
        WilmaLessonNoteParser.parseWilmaLessonNotes(htmlDoc)
    }
}