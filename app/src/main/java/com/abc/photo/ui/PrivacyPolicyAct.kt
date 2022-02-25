package com.abc.photo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abc.photo.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        web.loadUrl("file:///android_asset/privacyPolicy.html")
    }
}