package com.abc.photo.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.abc.photo.R
import com.abc.photo.utils.ShareUtils
import com.lcw.library.imagepicker.provider.ImagePickerProvider
import com.sdsmdg.tastytoast.TastyToast
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private var mFilePath: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MMKV.initialize(this)
//        UpdateHelper.get().update(this)
        start.setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }
        myphoto.setOnClickListener {
            startActivity(Intent(this, PictureAct::class.java))
        }
        share.setOnClickListener {
            ShareUtils.get().shareWithNative(this)
        }
        options.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyAct::class.java))
        }
        camera.setOnClickListener {
            try {
                val fileDir = File(Environment.getExternalStorageDirectory(), "Pictures")
                if (!fileDir.exists()) {
                    fileDir.mkdir()
                }
                mFilePath = fileDir.absolutePath + "/IMG_" + System.currentTimeMillis() + ".jpg"

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri: Uri = if (Build.VERSION.SDK_INT >= 24) {
                    FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this), File(mFilePath))
                } else {
                    Uri.fromFile(File(mFilePath))
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, 100)
            } catch (e: Exception) {
                TastyToast.makeText(this, "camera error", TastyToast.LENGTH_SHORT, TastyToast.ERROR)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            try {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$mFilePath")))
            } catch (e: Exception) {
                TastyToast.makeText(this, "scan imgs error", TastyToast.LENGTH_SHORT, TastyToast.ERROR)
            }

        }
    }
}