package org.openwilma.kotlin.methods

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.WilmaSession
import java.time.LocalDate

class ScheduleKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private lateinit var session: WilmaSession

    @BeforeEach
    internal fun setUp() = runBlocking {
        session = OpenWilma.signIn(wilmaServer, "oppilas", "oppilas")
    }

    @Test
    fun getSchedule() = runBlocking {
        val schedule = OpenWilma.getSchedule(session)
        println(Gson().toJson(schedule))
    }

    @Test
    fun getScheduleInRange() = runBlocking {
        val schedule = OpenWilma.getScheduleRange(session, LocalDate.now(), LocalDate.now().plusDays(12))
        println(Gson().toJson(schedule))
    }
}