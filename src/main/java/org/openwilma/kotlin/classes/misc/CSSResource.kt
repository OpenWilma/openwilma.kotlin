package org.openwilma.kotlin.classes.misc

internal class CSSResource {
    var typeIds: List<String>
    var cssParams: HashMap<String, String>

    constructor(trayItemIds: List<String>, cssParams: HashMap<String, String>) {
        this.typeIds = trayItemIds
        this.cssParams = cssParams
    }

    constructor(trayItemIds: List<String>) {
        this.typeIds = trayItemIds
        cssParams = HashMap()
    }
}