package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.Response
import org.apache.commons.io.IOUtils
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.URLUtils
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


public suspend fun getUserAccount(wilmaSession: WilmaSession): WilmaAPIResponse<WilmaAccountInfo> {
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


public suspend fun getRoles(wilmaSession: WilmaSession): WilmaAPIResponse<List<WilmaRole>> {
    val roles: WilmaAPIResponse<List<WilmaRole>> = suspendCoroutine {
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
    roles.payload?.let {it.forEach {role -> role.profilePicture = getRolePFP(wilmaSession, role) }}
    return roles
}

public suspend fun getActiveUserFormKey(wilmaSession: WilmaSession, roleRequired: Boolean = false): String? {
    return getActiveRole(wilmaSession, roleRequired)?.formKey
}

private suspend fun getRolePFP(wilmaSession: WilmaSession, role: WilmaRole, noException: Boolean =false): String {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient(wilmaSession)
        httpClient.getRawRequest(URLUtils.buildUrlWithRole(wilmaSession, role, "profiles/photo"),  object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {}

            override fun onRawResponse(response: Response) {
                if (response.code == 200) {
                    val inputStream = response.body?.byteStream()
                    try {
                        it.resume(Base64.getEncoder().encodeToString(IOUtils.toByteArray((inputStream))))
                    } catch (e: Exception) {
                        if (noException) {
                            it.resume("")
                            return
                        }
                        it.resumeWithException(e)
                    }
                    return
                }
                it.resume("")
            }

            override fun onFailed(error: Error) {
                if (noException) {
                    it.resume("")
                    return
                }
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun getActiveRole(wilmaSession: WilmaSession, roleRequired: Boolean = false): WilmaRole? {
    val role: WilmaRole? =  suspendCoroutine {
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
                } else if (wilmaSession.getAccountInfo() != null) {
                    it.resume(jsonResponse.payload?.find { newRole -> newRole.primusId == wilmaSession.getAccountInfo()?.primusId && newRole.type == UserType.WILMA_ACCOUNT })
                } else {
                    it.resume(jsonResponse.payload?.first { payloadRole -> payloadRole.type != UserType.WILMA_ACCOUNT })
                }
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
    role?.let { it.profilePicture = getRolePFP(wilmaSession, it, true) }
    return role
}