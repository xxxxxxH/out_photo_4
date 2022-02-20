package com.abc.photo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abc.lib.u.CommonUtils
import com.abc.lib.u.UpdateHelper
import com.alertdialogpro.AlertDialogPro

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UpdateHelper.get().update(this)
    }
}