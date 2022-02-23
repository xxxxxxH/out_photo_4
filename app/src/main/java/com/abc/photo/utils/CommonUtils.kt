package com.abc.photo.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream


object CommonUtils {
    private fun getImageFromAssetsFile(context: Context,filename: String): Bitmap? {
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
}