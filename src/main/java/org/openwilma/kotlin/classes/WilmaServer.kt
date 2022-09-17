package org.openwilma.kotlin.classes

import com.google.gson.annotations.SerializedName

class WilmaServer {
    @SerializedName("url")
    var serverURL: String

    @SerializedName("name")
    var name: String? = null

    constructor(serverURL: String, name: String?) {
        this.serverURL = serverURL
        this.name = name
    }

    constructor(serverURL: String) {
        this.serverURL = serverURL
    }
}