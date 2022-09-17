package org.openwilma.kotlin.classes.user

import com.google.gson.annotations.SerializedName
import java.util.*

data class WilmaAccountInfo(
    @field:SerializedName("id") var primusId: Int,
    @field:SerializedName("firstname") var firstName: String,
    @field:SerializedName("lastname") var lastName: String,
    @field:SerializedName("username") var username: String,
    @field:SerializedName("lastLogin") var lastLogin: Date,
    @field:SerializedName("multiFactorAuthentication") var mfaEnabled: Boolean
)
