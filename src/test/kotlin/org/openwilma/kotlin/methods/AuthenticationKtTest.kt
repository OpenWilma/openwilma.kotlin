package org.openwilma.kotlin.methods

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaServer


internal class AuthenticationKtTest {

    private val wilmaServer = WilmaServer("https://espoondemo.inschool.fi")

    @Test
    fun getSessionId(): Unit = runBlocking {
        val sessionResp = OpenWilma.getSessionId(wilmaServer)
        println(Gson().toJson(sessionResp))
        assert(sessionResp.sessionId.isNotEmpty())
    }

    @Test
    fun signInUsingBasicAccount(): Unit = runBlocking {
        val wilmaSession = OpenWilma.signIn(wilmaServer, "oppilas", "oppilas")
        println(Gson().toJson(wilmaSession))
        assert(wilmaSession.getAccountInfo() == null)
    }

    @Test
    fun signInUsingRoleAccount(): Unit = runBlocking {
        val wilmaSession = OpenWilma.signIn(wilmaServer, "ope", "ope")
        assert(wilmaSession.getAccountInfo() != null)
    }

    @Test
    fun getRoles(): Unit = runBlocking {
        val wilmaSession = OpenWilma.signIn(wilmaServer, "ope", "ope")
        val roles = OpenWilma.getRoles(wilmaSession)
        println(roles)
        assert(roles.payload?.isNotEmpty() ?: false)
    }
}