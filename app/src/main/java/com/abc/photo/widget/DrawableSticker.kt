package net.widget

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log

open class DrawableSticker(override var drawable: Drawable?) : Sticker() {
    private val mRealBounds: Rect
    var tag: String? = null
    override fun draw(canvas: Canvas?) {
        canvas!!.save()
        canvas.concat(mMatrix)
        drawable!!.bounds = mRealBounds
        drawable!!.draw(canvas)
        canvas.restore()
    }

    override val width: Int
        get() {
            try {
                Log.i("TAG", "sticker width  : " + drawable!!.intrinsicWidth)
                return drawable!!.intrinsicWidth
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            return 0
        }
    override val height: Int
        get() {
            try {
                Log.i("TAG", "sticker height  : " + drawable!!.intrinsicHeight)
                return drawable!!.intrinsicHeight
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            return 0
        }

    override fun release() {
        if (drawable != null) {
            drawable = null
        }
    }

    init {
        mMatrix = Matrix()
        mRealBounds = Rect(0, 0, width, height)
    }
}