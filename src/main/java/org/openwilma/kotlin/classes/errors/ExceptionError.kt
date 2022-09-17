package org.openwilma.kotlin.classes.errors

class ExceptionError(var exception: Exception) : Error(
    exception.message, ErrorType.Unknown
)