package org.openwilma.kotlin.config

object Config {
    const val wilmaServersURL = "https://wilmahub.service.inschool.fi/wilmat"
    const val sessionRegex = "^(.*)Wilma2SID=([^;]+)(.*)$"
    const val slugRegex = "!(0[0-9])([0-9]+)"
    const val jsonQuery = "format=json"
}