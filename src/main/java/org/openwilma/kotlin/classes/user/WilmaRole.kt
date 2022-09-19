package org.openwilma.kotlin.classes.user

import com.google.gson.annotations.SerializedName
import org.openwilma.kotlin.enums.UserType

data class WilmaRole(
    @field:SerializedName("name") var name: String,
    @field:SerializedName("type") var type: UserType,
    @field:SerializedName("primusId") var primusId: Int,
    @field:SerializedName("slug") var slug: String?,
    @field:SerializedName("formKey") var formKey: String?,
    @field:SerializedName("schools") var schools: List<WilmaSchool>,
    var profilePicture: String? = null
)
