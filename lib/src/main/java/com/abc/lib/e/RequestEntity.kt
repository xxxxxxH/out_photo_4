package com.xxxxxxh.lib.entity

import java.io.Serializable


data class RequestEntity(
    var appName: String = "",
    var appId: String = "",
    var applink: String = "",
    var ref: String = "",
    var token: String = "",
    var istatus: Boolean = false
):Serializable
