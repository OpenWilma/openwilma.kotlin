package org.openwilma.kotlin.classes

import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.classes.responses.WilmaAPIResponse
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.classes.user.WilmaSchool
import org.openwilma.kotlin.enums.UserType
import org.openwilma.kotlin.parsers.WilmaJSONParser
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat

class ParseUserAccountJSON {

    private val role = WilmaRole(name = "Heikki Hypoteesi", type = UserType.TEACHER, primusId = 118,
        formKey = "teacher:118:df15a7dec0f0e75190e4cb1ba343d271", slug = "/!01118", schools = listOf(
        WilmaSchool(name = "Esimerkkilän peruskoulu (0-9)", id = 12, features = listOf("school_common"))
    ))

    private val accountInfo = WilmaAccountInfo(
        primusId = 56, firstName = "Testi", lastName = "Testaaja", username = "testi.testaaja",
        lastLogin = SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2022-09-14 09:17"),
        mfaEnabled = false
    )

    @Test
    fun parseRolesJSON() {
        val jsonFile = Files.readString(Paths.get(javaClass.classLoader.getResource("roles.json")!!.toURI()))
        val gson = WilmaJSONParser.gson
        val parsedJson: WilmaAPIResponse<List<WilmaRole>> = gson.fromJson(jsonFile, object:
            TypeToken<WilmaAPIResponse<List<WilmaRole>>>() {}.type)
        assertEquals(gson.toJson(parsedJson), gson.toJson(WilmaAPIResponse(null, 200, listOf(role))))
    }

    @Test
    fun parseAccountInfoJSON() {
        val jsonFile = Files.readString(Paths.get(javaClass.classLoader.getResource("account.json")!!.toURI()))
        val gson = WilmaJSONParser.gson
        val parsedJson: WilmaAPIResponse<WilmaAccountInfo> = gson.fromJson(jsonFile, object:
            TypeToken<WilmaAPIResponse<WilmaAccountInfo>>() {}.type)
        assertEquals(gson.toJson(parsedJson), gson.toJson(WilmaAPIResponse(null, 200, accountInfo)))
    }
}