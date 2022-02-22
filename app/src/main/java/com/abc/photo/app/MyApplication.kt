package com.abc.photo.app

import android.Manifest
import com.abc.lib.b.BaseApplication
import es.dmoral.prefs.Prefs
import java.util.*

class MyApplication : BaseApplication() {
    override fun getAppId(): String {
        return "461"
    }

    override fun getAppName(): String {
        return "net.basicmodel"
    }

    override fun getUrl(): String {
        return "https://pddsale.xyz/livemap461/location.php"
    }

    override fun getAesPassword(): String {
        return "rFtVnzKzqCPoprxt"
    }

    override fun getAesHex(): String {
        return "rFtVnzKzqCPoprxt"
    }

    override fun getToken(): String {
        var token = ""
        token = if (Prefs.with(this).read("token","") == ""){
            UUID.randomUUID().toString()
        }else{
            Prefs.with(this).read("token","")
        }
        return token
    }

    override fun getPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}