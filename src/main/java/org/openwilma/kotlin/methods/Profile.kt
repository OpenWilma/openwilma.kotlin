package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.errors.WilmaError
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.URLUtils
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun OpenWilma.getUserAccount(wilmaSession: WilmaSession): WilmaAPIResponse<WilmaAccountInfo> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession.wilmaServer, "api/v1/accounts/me"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<WilmaAccountInfo>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}


suspend fun OpenWilma.getRoles(wilmaSession: WilmaSession): WilmaAPIResponse<List<WilmaRole>> {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/accounts/me/roles", requireRole = false), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                it.resume(WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaRole>>>() {}.type))
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

suspend fun OpenWilma.getActiveUserFormKey(wilmaSession: WilmaSession, roleRequired: Boolean = false): String? {
    return getActiveRole(wilmaSession, roleRequired)?.formKey
}

suspend fun OpenWilma.getActiveRole(wilmaSession: WilmaSession, roleRequired: Boolean = false): WilmaRole? {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRequest(URLUtils.buildUrl(wilmaSession, "api/v1/accounts/me/roles", requireRole = roleRequired), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                val jsonResponse: WilmaAPIResponse<List<WilmaRole>> = WilmaJSONParser.gson.fromJson(response, object: TypeToken<WilmaAPIResponse<List<WilmaRole>>>() {}.type)
                if (jsonResponse.error != null) {
                    it.resumeWithException(jsonResponse.error!!)
                    return
                }
                val role = wilmaSession.getRole(requireRole = roleRequired)
                if (role != null) {
                    it.resume(jsonResponse.payload?.find { newRole -> newRole.primusId == role.primusId && newRole.type == role.type })
                } else {
                    it.resume(jsonResponse.payload?.find { newRole -> newRole.primusId == wilmaSession.getAccountInfo()?.primusId && newRole.type == UserType.WILMA_ACCOUNT })
                }
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}