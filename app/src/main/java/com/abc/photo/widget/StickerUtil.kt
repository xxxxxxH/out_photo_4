package net.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

object StickerUtil {
    private const val TAG = "StickerView"
    fun saveImageToGallery(file: File, bmp: Bitmap?): String {
        if (bmp == null) {
            Log.e(TAG, "saveImageToGallery: the bitmap is null")
            return ""
        }
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.e(TAG, "saveImageToGallery: the path of bmp is " + file.absolutePath)
        return file.absolutePath
    }

    // 把文件插入到系统图库
    fun notifySystemGallery(context: Context, file: File?) {
        if (file == null || !file.exists()) {
            Log.e(TAG, "notifySystemGallery: the file do not exist.")
            return
        }
        try {
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath, file.name, null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
    }

    fun trapToRect(array: FloatArray): RectF {
        val r = RectF(
            Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY,
            Float.NEGATIVE_INFINITY,
            Float.NEGATIVE_INFINITY
        )
        var i = 1
        while (i < array.size) {
            val x = (array[i - 1] * 10).roundToInt() / 10f
            val y = (array[i] * 10).roundToInt() / 10f
            r.left = if (x < r.left) x else r.left
            r.top = if (y < r.top) y else r.top
            r.right = if (x > r.right) x else r.right
            r.bottom = if (y > r.bottom) y else r.bottom
            i += 2
        }
        r.sort()
        return r
    }
}