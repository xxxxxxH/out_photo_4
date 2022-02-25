package com.abc.photo.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.view.View
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


object CommonUtils {
    //    var bitmap:Bitmap?=null
    private fun getImageFromAssetsFile(context: Context, filename: String): Bitmap? {
        var filename = filename
        filename = "picture_icon/$filename"
        var mBitmap: Bitmap? = null
        val mAssetManager: AssetManager = context.resources.assets
        try {
            val mInputStream: InputStream = mAssetManager.open(filename)
            mBitmap = BitmapFactory.decodeStream(mInputStream)
            mInputStream.close()
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            mBitmap = null
        }
        return mBitmap
    }

    fun creteProgressDialog(context: Context): AwesomeProgressDialog {
        val dialog = AwesomeProgressDialog(context)
        dialog.setMessage("Please wait")
            .setTitle("Tips")
            .setCancelable(false)
        return dialog
    }

    fun createBitmapFromView(view: View) {
        view.clearFocus()
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        if (bitmap != null) {
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
        }
        val path = File(Environment.getExternalStorageDirectory().path + File.separator)
        val fileName = System.currentTimeMillis().toString()
        val imgFile = File(path, "$fileName.png")
        if (!imgFile.exists())
            imgFile.createNewFile()
        var fos: FileOutputStream? = null
        try {
            if (bitmap == null) return
            fos = FileOutputStream(imgFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            scanNotice(view.context, imgFile)
            EventBus.getDefault().post(MessageEvent("saveSuccess"))
            saveKeys("keys", fileName)
            MMKV.defaultMMKV()!!.encode(fileName, imgFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            EventBus.getDefault().post(MessageEvent("saveError"))
        } finally {
            fos?.flush()
            fos!!.close()
        }
    }

     fun scanNotice(context: Context, file: File) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            null,
            object : MediaScannerConnection.MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {}
                override fun onScanCompleted(path: String, uri: Uri) {}
            })
    }

    fun saveKeys(key: String, keyValues: String) {
        var keys = MMKV.defaultMMKV()!!.decodeStringSet(key)
        if (keys == null) {
            keys = HashSet()
        }
        keys.add(keyValues)
        MMKV.defaultMMKV()!!.encode(key, keys)
    }

    fun BitmapToBytes(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun BytesToBitmap(bis: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bis, 0, bis.size)
    }
}