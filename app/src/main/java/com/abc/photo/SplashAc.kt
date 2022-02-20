package com.abc.photo

import android.content.Intent
import com.abc.lib.b.BaseActivity

class SplashAc : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun showLoading() {
    }

    override fun closeLoading() {
    }
}