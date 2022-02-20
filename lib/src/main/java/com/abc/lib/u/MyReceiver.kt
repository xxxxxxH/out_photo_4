package com.abc.lib.u

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import es.dmoral.prefs.Prefs

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_PACKAGE_ADDED) {
            val data = intent.dataString.toString()
            data.let {
                if (data.contains(context!!.packageName.toString())) {
                    Prefs.with(context).writeBoolean("state", true)
                }
            }
        }
    }
}