package com.abc.photo.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics

class ScreenUtils {
    companion object {
        private var instance: ScreenUtils? = null
            get() {
                field ?: run {
                    field = ScreenUtils()
                }
                return field
            }

        @Synchronized
        fun get(): ScreenUtils {
            return instance!!
        }
    }

    fun getScreenSize(activity: Activity):IntArray{
        val result = IntArray(2)
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        result[0] = height
        result[1] = width
        return result
    }
}