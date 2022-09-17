package org.openwilma.kotlin.utils

import org.openwilma.kotlin.classes.WilmaServer
import org.openwilma.kotlin.classes.WilmaSession
import org.openwilma.kotlin.config.Config
import java.net.URI


object URLUtils {
    fun buildUrl(wilmaServer: WilmaServer, path: String): String {
        var serverUrl = wilmaServer.serverURL
        serverUrl = if (serverUrl.endsWith("/")) serverUrl + path else "$serverUrl/$path"
        return serverUrl
    }

    fun buildUrl(wilmaSession: WilmaSession, path: String, requireRole: Boolean = true): String {
        var serverUrl = wilmaSession.wilmaServer.serverURL
        var slug = wilmaSession.getRole(requireRole)?.slug ?: ""
        if (slug.isNotEmpty() && slug[0] == '/') {
            slug = slug.drop(1)+"/"
        }
        serverUrl = if (serverUrl.endsWith("/")) "$serverUrl$slug$path" else "$serverUrl/$slug$path"
        return serverUrl
    }

    fun buildUrlWithJsonFormat(wilmaServer: WilmaServer, path: String): String {
        var serverUrl = wilmaServer.serverURL
        serverUrl = if (serverUrl.endsWith("/")) serverUrl + path else "$serverUrl/$path"
        return try {
            val oldUri = URI(serverUrl)
            var newQuery: String = oldUri.query
            newQuery += "&" + Config.jsonQuery
            val patchedUri = URI(
                oldUri.scheme, oldUri.authority,
                oldUri.path, newQuery, oldUri.fragment
            )
            patchedUri.toString()
        } catch (ignored: Exception) {
            serverUrl
        }
    }
}