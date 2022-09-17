package org.openwilma.kotlin.classes

import org.openwilma.kotlin.classes.errors.Error
import org.openwilma.kotlin.classes.errors.ErrorType
import org.openwilma.kotlin.classes.user.WilmaAccountInfo
import org.openwilma.kotlin.classes.user.WilmaRole
import org.openwilma.kotlin.enums.UserType

class WilmaSession(var wilmaServer: WilmaServer, var sessionId: String, private val accountInfo: WilmaAccountInfo?, private var role: WilmaRole? = null) {

    fun getRole(requireRole: Boolean = true): WilmaRole? {
        if ((role == null || role?.type == UserType.WILMA_ACCOUNT) && accountInfo != null && requireRole) throw Error("Valid role is required for this account", ErrorType.RoleRequired)
        return role
    }

    fun getAccountInfo() = accountInfo

    fun setRole(role: WilmaRole) {
        this.role = role
    }
}