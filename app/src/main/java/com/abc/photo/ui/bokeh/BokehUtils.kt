package com.abc.photo.ui.bokeh

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object  BokehUtils {

    fun getBokehRes(context: Context): ArrayList<Bitmap> {
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("blend")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("blend/$item"))
                result.add(b)
//                b.recycle()
            }
        }
        return result
    }
}