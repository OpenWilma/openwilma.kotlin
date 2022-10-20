package org.openwilma.kotlin.methods

import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.Response
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.errors.CredentialsError
import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.errors.ErrorType
import org.openwilma.kotlin.classes.errors.MFAError
import org.openwilma.kotlin.classes.responses.JSONErrorResponse
import org.openwilma.kotlin.classes.responses.SessionResponse
import org.openwilma.kotlin.clients.WilmaHttpClient
import org.openwilma.kotlin.parsers.WilmaJSONParser
import org.openwilma.kotlin.utils.JSONUtils
import org.openwilma.kotlin.utils.URLUtils
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public suspend fun getSessionId(wilmaServer: WilmaServer, skipVersionValidation: Boolean = false): SessionResponse {
    return suspendCoroutine {
        val httpClient = WilmaHttpClient()
        httpClient.getRequest(URLUtils.buildUrl(wilmaServer, "index_json"), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {
                val responseObject: SessionResponse = WilmaJSONParser.gson.fromJson(response, object: TypeToken<SessionResponse>() {}.type)
                if (responseObject.apiVersion < OpenWilma.minimumSupportedWilmaVersion && !skipVersionValidation) {
                    it.resumeWithException(Error("Wilma version ${responseObject.apiVersion} is not supported. Minimum supported version is $OpenWilma.minimumSupportedWilmaVersion", ErrorType.UnsupportedServer))
                    return
                }
                it.resume(responseObject)
            }

            override fun onRawResponse(response: Response) {}

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
}

public suspend fun signIn(wilmaServer: WilmaServer, username: String, password: String, skipVersionValidation: Boolean = false): WilmaSession {
    val sessionId = getSessionId(wilmaServer, skipVersionValidation = skipVersionValidation)
    val cookie: String = suspendCoroutine {
        val httpClient = WilmaHttpClient(false)
        val formBuilder: FormBody.Builder = FormBody.Builder()
        // Login parameters
        formBuilder.add("Login", username)
        formBuilder.add("Password", password)
        formBuilder.add("SESSIONID", sessionId.sessionId)
        formBuilder.add("CompleteJson", "")
        formBuilder.add("format", "json")

        httpClient.postRawRequest(URLUtils.buildUrl(wilmaServer, "login"), formBuilder.build(), object : WilmaHttpClient.HttpClientInterface {
            override fun onResponse(response: String, status: Int) {}

            override fun onRawResponse(response: Response) {
                response.body?.let { resp ->
                    val content = resp.string()
                    resp.close()
                    if (JSONUtils.isJSONValid(content)) {
                        val errorResponse = WilmaJSONParser.gson.fromJson<JSONErrorResponse>(content, object: TypeToken<JSONErrorResponse>() {}.type)
                        errorResponse.wilmaError?.let {wilmaError ->
                            it.resumeWithException(wilmaError)
                            return
                        }
                    }
                }
                if (response.headers.names().contains("Location")) {
                    val location = response.headers["Location"]!!

                    val parsedUrl = URL(location)
                    if (parsedUrl.path.startsWith("/") || parsedUrl.path.startsWith("/!")) {
                        // Check for invalid credentials
                        if (parsedUrl.query.contains("loginfailed")) {
                            it.resumeWithException(CredentialsError())
                            return
                        }

                        // Check for session cookies
                        if (parsedUrl.query.contains("checkcookie")) {
                            if (response.headers.names().contains("Set-Cookie")) {
                                val sessionCookie: String? = response.headers("Set-Cookie")
                                    .find {cookie -> cookie.contains("Wilma2SID=")}
                                if (sessionCookie == null) {
                                    it.resumeWithException(Error("Unable to parse session cookie", ErrorType.InvalidContent))
                                    return
                                }
                                it.resume(sessionCookie)
                                return
                            }
                            it.resumeWithException(Error("Session cookie headers missing", ErrorType.NoContent))
                            return
                        }

                        it.resumeWithException(Error("Sign in failed", ErrorType.Unknown))
                    } else if (parsedUrl.path == "/mfa") {
                        it.resumeWithException(MFAError())
                    } else {
                        it.resumeWithException(Error("Unrecognized redirect: ${parsedUrl.path}", ErrorType.InvalidContent))
                    }
                } else {
                    it.resumeWithException(Error("No redirect header", ErrorType.InvalidContent))
                }
            }

            override fun onFailed(error: Error) {
                it.resumeWithException(error)
            }
        })
    }
    val wilmaSession = WilmaSession(wilmaServer, cookie, null, null)
    val userInfo = getUserAccount(wilmaSession)
    if (userInfo.payload == null) {
        val roles = getRoles(wilmaSession)
        if (roles.payload?.isNotEmpty() == true) {
            return WilmaSession(wilmaServer, cookie, userInfo.payload, roles.payload!!.first())
        }
        throw Error("Could not find account role!", ErrorType.InvalidContent)
    } else {
        // Wilma MFA not yet supported
        if (userInfo.payload?.mfaEnabled == true) {
            throw MFAError()
        }
        return WilmaSession(wilmaServer, cookie, userInfo.payload)
    }
}