package com.abc.photo.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.abc.photo.R
import com.abc.photo.ui.bokeh.BokehActivity
import com.abc.photo.ui.color.ColorActivity
import com.abc.photo.ui.pip.PipActivity
import com.abc.photo.ui.pixel.PixelActivity
import com.abc.photo.ui.shatter.ShatterActivity
import com.abc.photo.utils.GlideLoader
import com.lcw.library.imagepicker.ImagePicker

class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
    }

    fun option(view: View) {
        val desc = view.contentDescription.toString().toInt()
        selectImage(desc)
    }

    private fun selectImage(code: Int) {
        ImagePicker.getInstance()
            .setTitle("select")
            .showCamera(true)
            .showVideo(false)
            .showImage(true)
            .setSingleType(true)
            .setImageLoader(GlideLoader())
            .start(this, code)
    }

    private fun startNextActivity(clazz: Class<*>, url: String) {
        val i = Intent(this, clazz)
        i.putExtra("url", url)
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                val url: String =
                    (it.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES) as ArrayList<String>)[0]
                if (TextUtils.isEmpty(url))
                    return@let
                when (requestCode) {
                    1 -> {
                        startNextActivity(BokehActivity::class.java, url)
                    }
                    2 -> {
                        startNextActivity(ColorActivity::class.java, url)
                    }
                    3 -> {
                        startNextActivity(PipActivity::class.java, url)
                    }
                    4 -> {
                        startNextActivity(PixelActivity::class.java, url)
                    }
                    5 -> {
                        startNextActivity(ShatterActivity::class.java, url)
                    }
                }
            }
        }
    }
}