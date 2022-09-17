package org.openwilma.kotlin.classes.errors

class NetworkError(private val exception: Exception) : Error(
    exception.message, ErrorType.NetworkError
)