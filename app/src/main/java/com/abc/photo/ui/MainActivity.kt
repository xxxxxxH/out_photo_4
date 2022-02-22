package com.abc.photo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abc.lib.u.UpdateHelper
import com.abc.photo.R
import com.lcw.library.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UpdateHelper.get().update(this)
        start.setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }

    }
}