package org.openwilma.kotlin.utils

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody


class DiskCacheEnforcerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val newResponseBody: ResponseBody = ResponseBody.create(response.body!!.contentType(), response.body!!.bytes())
        return response.newBuilder().body(newResponseBody).build()
    }
}