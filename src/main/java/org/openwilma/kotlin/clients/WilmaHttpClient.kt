package org.openwilma.kotlin.clients

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.errors.ErrorType
import org.openwilma.kotlin.classes.errors.ExpiredSessionError
import org.openwilma.kotlin.classes.errors.NetworkError
import org.openwilma.kotlin.utils.SessionUtils
import org.openwilma.kotlin.utils.UserAgentInterceptor
import org.openwilma.kotlin.utils.WilmaCookieJar
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class WilmaHttpClient {
    private val client: OkHttpClient
    private var containsSession by Delegates.notNull<Boolean>()

    constructor() {
        containsSession = false
        client = OkHttpClient().newBuilder().callTimeout(60, TimeUnit.SECONDS).addInterceptor(UserAgentInterceptor()).build()
    }

    constructor(wilmaSession: WilmaSession) {
        containsSession = true
        client = OkHttpClient().newBuilder().cookieJar(WilmaCookieJar.getWilmaCookieJar(wilmaSession))
            .callTimeout(60, TimeUnit.SECONDS).addInterceptor(UserAgentInterceptor()).build()
    }

    constructor(followRedirects: Boolean) {
        containsSession = false
        client = OkHttpClient().newBuilder().followRedirects(followRedirects).followSslRedirects(followRedirects)
            .callTimeout(60, TimeUnit.SECONDS).addInterceptor(UserAgentInterceptor()).build()
    }

    interface HttpClientInterface {
        fun onResponse(response: String, status: Int)
        fun onRawResponse(response: Response)
        fun onFailed(error: Error)
    }

    fun getRequest(url: String, httpClientInterface: HttpClientInterface) {
        val requestBuilder = Request.Builder()
        requestBuilder.url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            val body = response.body
            if (body != null) {
                val content = body.string()
                if (containsSession) {
                    SessionUtils.checkSessionExpiration(content)
                }
                body.close()
                httpClientInterface.onResponse(content, response.code)
            } else {
                httpClientInterface.onFailed(Error("No content in response", ErrorType.NoContent))
            }
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        } catch (e: ExpiredSessionError) {
            httpClientInterface.onFailed(e)
        }
    }

    fun getRawRequest(url: String, httpClientInterface: HttpClientInterface) {
        val requestBuilder = Request.Builder()
        requestBuilder.url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            httpClientInterface.onRawResponse(response)
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        }
    }

    fun postRequest(url: String, requestBody: RequestBody, httpClientInterface: HttpClientInterface) {
        val requestBuilder = Request.Builder()
        requestBuilder
            .post(requestBody)
            .url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            val body = response.body
            if (body != null) {
                val content = body.string()
                if (containsSession) {
                    SessionUtils.checkSessionExpiration(content)
                }
                body.close()
                httpClientInterface.onResponse(content, response.code)
            } else {
                httpClientInterface.onFailed(Error("No content in response", ErrorType.NoContent))
            }
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        } catch (e: ExpiredSessionError) {
            httpClientInterface.onFailed(e)
        }
    }

    fun postRawRequest(url: String, requestBody: RequestBody, httpClientInterface: HttpClientInterface) {
        val requestBuilder = Request.Builder()
        requestBuilder
            .post(requestBody)
            .url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            httpClientInterface.onRawResponse(response)
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        }
    }
}