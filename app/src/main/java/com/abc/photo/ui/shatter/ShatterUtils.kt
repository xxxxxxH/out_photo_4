package com.abc.photo.ui.shatter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object ShatterUtils {
    fun getShatter(context: Context):ArrayList<Bitmap>{
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("shatter")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("shatter/$item"))
                result.add(b)
            }
        }
        return result
    }

    fun getStickers(context: Context):ArrayList<Bitmap>{
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("sticker")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("sticker/$item"))
                result.add(b)
            }
        }
        return result
    }

}