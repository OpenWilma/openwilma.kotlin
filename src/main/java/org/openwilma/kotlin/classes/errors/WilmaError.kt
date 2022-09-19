package org.openwilma.kotlin.classes.errors

import com.google.gson.annotations.SerializedName
import java.util.StringJoiner

class WilmaError(
    message: String?, @field:SerializedName("id") var errorID: String, @field:SerializedName(
        "description"
    ) var description: String, @field:SerializedName("whatnext") var whatsnext: String
) : Error(message, ErrorType.WilmaError) {

    override fun getErrorType(): ErrorType {
        return ErrorType.WilmaError
    }
}