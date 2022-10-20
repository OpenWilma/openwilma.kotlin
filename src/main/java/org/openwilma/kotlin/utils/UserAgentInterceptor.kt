package org.openwilma.kotlin.utils

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma


class UserAgentInterceptor(private val userAgent: String = OpenWilma.customUserAgent ?: "OpenWilma/${OpenWilma.versionName} (kotlin)") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithUserAgent: Request = originalRequest.newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}