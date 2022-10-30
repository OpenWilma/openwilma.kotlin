package org.openwilma.kotlin.methods

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer
import java.time.LocalDate

class ScheduleKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    private var client = OpenWilma()

    @BeforeEach
    internal fun setUp(): Unit = runBlocking {
        client.signInToWilma(wilmaServer, "oppilas", "oppilas")
    }

    @Test
    fun getSchedule() = runBlocking {
        val schedule = client.schedule()
        println(Gson().toJson(schedule))
    }

    @Test
    fun getScheduleInRange() = runBlocking {
        val schedule = client.scheduleRange(LocalDate.now(), LocalDate.now().plusDays(12))
        println(Gson().toJson(schedule))
    }
}