package org.openwilma.kotlin.classes.errors

open class Error(override var message: String?, private var errorType: ErrorType): Throwable() {
    open fun getErrorType(): ErrorType {
        return errorType
    }

    fun setErrorType(errorType: ErrorType) {
        this.errorType = errorType
    }
}