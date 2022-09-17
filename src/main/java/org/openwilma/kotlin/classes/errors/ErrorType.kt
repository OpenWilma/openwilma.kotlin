package org.openwilma.kotlin.classes.errors

enum class ErrorType(var errorCode: Int) {
    Unknown(-1), NetworkError(0), InvalidContent(1), WilmaError(2),
    NoContent(3), LoginError(4), RoleRequired(5), ExpiredSession(6), MFARequired(7), UnsupportedServer(8);

}