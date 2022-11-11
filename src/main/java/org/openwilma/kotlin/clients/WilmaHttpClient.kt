package org.openwilma.kotlin.clients

import okhttp3.*
import okhttp3.internal.closeQuietly
import okhttp3.logging.HttpLoggingInterceptor
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.errors.ErrorType
import org.openwilma.kotlin.classes.errors.ExpiredSessionError
import org.openwilma.kotlin.classes.errors.NetworkError
import org.openwilma.kotlin.utils.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class WilmaHttpClient private constructor(followRedirects: Boolean = true) {
    private val client: OkHttpClient
    private val cachedClient: OkHttpClient

    companion object {
        private var basicSingletonInstance: WilmaHttpClient? = null
        private var basicSingletonInstanceNoRedirect: WilmaHttpClient? = null
        fun getInstance(): WilmaHttpClient {
            if (basicSingletonInstance == null) {
                basicSingletonInstance = WilmaHttpClient()
            }
            return basicSingletonInstance!!
        }

        fun getInstance(followRedirects: Boolean): WilmaHttpClient {
            if (!followRedirects) {
                if (basicSingletonInstanceNoRedirect == null) {
                    basicSingletonInstanceNoRedirect = WilmaHttpClient(false)
                }
                return basicSingletonInstanceNoRedirect!!
            }
            if (basicSingletonInstance == null) {
                basicSingletonInstance = WilmaHttpClient()
            }
            return basicSingletonInstance!!
        }
    }

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.NONE)
        client = OkHttpClient().newBuilder().retryOnConnectionFailure(true).followRedirects(followRedirects).followSslRedirects(followRedirects)
            .addInterceptor(logging)
            .callTimeout(60, TimeUnit.SECONDS).addInterceptor(UserAgentInterceptor()).build()
        cachedClient = OkHttpClient().newBuilder().retryOnConnectionFailure(true).followRedirects(followRedirects).followSslRedirects(followRedirects)
            .addInterceptor(logging)
            .callTimeout(60, TimeUnit.SECONDS).addInterceptor(UserAgentInterceptor())
            .cache(
                Cache(
                    directory = File(OpenWilma.cacheDirectory),
                    maxSize = 200L * 1024L * 1024L
                )
            ).addInterceptor(CacheInterceptor())
            .addInterceptor(DiskCacheEnforcerInterceptor()).build()
    }

    interface HttpClientInterface {
        fun onResponse(response: String, status: Int)
        fun onRawResponse(response: Response)
        fun onFailed(error: Error)
    }
    
    private fun wilmaRequestBuilder(wilmaSession: WilmaSession?): Request.Builder {
        val requestBuilder = Request.Builder()
        wilmaSession?.let {
            requestBuilder.header("Cookie", WilmaSessionUtils.getWilmaCookies(it).joinToString(" ") { cookie -> cookie.toString() })
        }
        return requestBuilder;
    }

    fun getRequest(url: String, wilmaSession: WilmaSession? = null, httpClientInterface: HttpClientInterface) {
        val requestBuilder = wilmaRequestBuilder(wilmaSession)
        requestBuilder.url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            val body = response.body
            if (body != null) {
                val content = body.string()
                body.closeQuietly()
                if (wilmaSession != null) {
                    SessionUtils.checkSessionExpiration(content)
                }
                httpClientInterface.onResponse(content, response.code)
            } else {
                httpClientInterface.onFailed(Error("No content in response", ErrorType.NoContent))
            }
            response.closeQuietly()
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        } catch (e: ExpiredSessionError) {
            httpClientInterface.onFailed(e)
        }
    }

    fun getCachedRequest(url: String, wilmaSession: WilmaSession? = null, httpClientInterface: HttpClientInterface) {
        val requestBuilder = wilmaRequestBuilder(wilmaSession)
        requestBuilder.url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = cachedClient.newCall(getRequest).execute()
            val body = response.body
            if (body != null) {
                val content = body.string()
                if (wilmaSession != null) {
                    SessionUtils.checkSessionExpiration(content)
                }
                httpClientInterface.onResponse(content, response.code)
                body.closeQuietly()
            } else {
                httpClientInterface.onFailed(Error("No content in response", ErrorType.NoContent))
            }
            response.closeQuietly()
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        } catch (e: ExpiredSessionError) {
            httpClientInterface.onFailed(e)
        }
    }

    fun getRawRequest(url: String, wilmaSession: WilmaSession? = null, httpClientInterface: HttpClientInterface) {
        val requestBuilder = wilmaRequestBuilder(wilmaSession)
        requestBuilder.url(url)

        // Making the request
        val getRequest: Request = requestBuilder.build()
        try {
            val response = client.newCall(getRequest).execute()
            httpClientInterface.onRawResponse(response)
            response.closeQuietly()
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        }
    }

    fun postRequest(url: String, requestBody: RequestBody, wilmaSession: WilmaSession? = null, httpClientInterface: HttpClientInterface) {
        val requestBuilder = wilmaRequestBuilder(wilmaSession)
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
                body.closeQuietly()
                if (wilmaSession != null) {
                    SessionUtils.checkSessionExpiration(content)
                }
                httpClientInterface.onResponse(content, response.code)
            } else {
                httpClientInterface.onFailed(Error("No content in response", ErrorType.NoContent))
            }
            response.closeQuietly()
        } catch (e: IOException) {
            httpClientInterface.onFailed(NetworkError(e))
        } catch (e: ExpiredSessionError) {
            httpClientInterface.onFailed(e)
        }
    }

    fun postRawRequest(url: String, requestBody: RequestBody, wilmaSession: WilmaSession? = null, httpClientInterface: HttpClientInterface) {
        val requestBuilder = wilmaRequestBuilder(wilmaSession)
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