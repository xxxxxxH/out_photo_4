package com.abc.lib.b

import android.app.Application
import org.xutils.x

abstract class BaseApplication : Application() {

    companion object {
        var instance: BaseApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        x.Ext.init(this)
    }

    abstract fun getAppId(): String
    abstract fun getAppName(): String
    abstract fun getUrl(): String
    abstract fun getAesPassword(): String
    abstract fun getAesHex(): String
    abstract fun getToken(): String
    abstract fun getPermissions(): Array<String>

}