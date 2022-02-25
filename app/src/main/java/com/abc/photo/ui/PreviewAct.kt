package com.abc.photo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abc.photo.R
import com.abc.photo.utils.ShareUtils
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.layout_float.*

class PreviewAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val url = intent.getStringExtra("url") as String
        preview.displayImage(url)
        shareFb.setOnClickListener {
            ShareUtils.get().shareWithFb(this, url)
        }
        shareIns.setOnClickListener {
            ShareUtils.get().shareWithIns(this, url)
        }
        shareEmail.setOnClickListener {
            ShareUtils.get().shareWithEmail(this, url)
        }
        delete.setOnClickListener {
            val keySet = MMKV.defaultMMKV()!!.decodeStringSet("keys") as HashSet?
            var deleteKey = ""
            if (keySet != null) {
                for (item in keySet) {
                    val value = MMKV.defaultMMKV()!!.decodeString(item)
                    if (value == url) {
                        deleteKey = item
                        break
                    }
                }
                MMKV.defaultMMKV()!!.removeValueForKey(deleteKey)
            }
            finish()
        }
    }
}