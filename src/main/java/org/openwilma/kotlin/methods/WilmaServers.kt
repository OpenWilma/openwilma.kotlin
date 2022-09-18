package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.responses.WilmaServersResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.config.Config
import org.openwilma.kotlin.parsers.WilmaJSONParser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun OpenWilma.Companion.getWilmaServers(): WilmaServersResponse {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient()
        httpClient.getRequest(Config.wilmaServersURL, object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaServersResponse>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: org.openwilma.kotlin.classes.errors.Error) {
                it.resumeWithException(error)
            }
        })
    }
}