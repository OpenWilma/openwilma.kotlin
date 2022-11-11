package org.openwilma.kotlin.utils

import okhttp3.Cookie
import org.openwilma.kotlin.classes.WilmaSession
import java.net.HttpCookie
import java.net.URI

object WilmaSessionUtils {
    private fun getDomainName(url: String): String {
        return try {
            val uri = URI(url)
            val domain = uri.host
            if (domain.startsWith("www.")) domain.substring(4) else domain
        } catch (e: Exception) {
            url
        }
    }

    fun getWilmaCookies(wilmaSession: WilmaSession): List<Cookie> {
        val cookieBuilder = Cookie.Builder()
        val cookies: MutableList<Cookie> = ArrayList()
        try {
            val httpCookies = HttpCookie.parse(wilmaSession.sessionId)
            for (cookie in httpCookies) {
                cookies.add(cookieBuilder.name(cookie.name).value(cookie.value).path("/").domain(cookie.domain).build())
            }
        } catch (ignored: Exception) {
            val wilmaCookie: Cookie =
                cookieBuilder.name("Wilma2SID").value(SessionUtils.parseSessionCookie(wilmaSession.sessionId) ?: wilmaSession.sessionId).path("/").domain(
                    getDomainName(wilmaSession.wilmaServer.serverURL)
                ).build()
            cookies.add(wilmaCookie)
        }
        return cookies
    }
}