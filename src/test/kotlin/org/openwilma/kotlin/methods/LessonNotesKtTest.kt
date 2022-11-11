package org.openwilma.kotlin.methods

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.enums.LessonNoteRange
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LessonNotesKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private var client = OpenWilma()

    @BeforeEach
    internal fun setUp(): Unit = runBlocking {
        client.signInToWilma(wilmaServer, "oppilas", "oppilas")
    }


    @Test
    fun testLessonNotes() = runBlocking {
        val lessonNotes = client.lessonNotes(dateRange = LessonNoteRange.CUSTOM, start = LocalDate.of(2016, 12, 9), end = LocalDate.of(2023, 6, 3))
        println(GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate::class.java, LocalDateGSONAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeGSONAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeGSONAdapter()).create().toJson(lessonNotes))
    }

    @Test
    fun testLessonNotePermissions() = runBlocking {
        assert(!client.canSaveLessonNoteExcuse())
        val huoltajaClient = OpenWilma()
        huoltajaClient.signInToWilma(wilmaServer, "hilla.huoltaja@example.fi", "huoltaja")
        huoltajaClient.wilmaSession.setRole(huoltajaClient.roles().payload?.filter { it.type != UserType.WILMA_ACCOUNT }?.get(0) ?: WilmaRole("", UserType.GUARDIAN, -1, "/!04806", "", listOf()))
        assert(huoltajaClient.canSaveLessonNoteExcuse())
    }
}