package net.widget

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.IntRange
import net.widget.StickerUtil.trapToRect
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Sticker {
    var mMatrix: Matrix? = null
    var isFlipped = false
    private val mMatrixValues = FloatArray(9)
    private val tag: String? = null
    var matrix: Matrix?
        get() = mMatrix
        set(matrix) {
            mMatrix!!.set(matrix)
        }

    abstract fun draw(canvas: Canvas?)
    abstract val width: Int
    abstract val height: Int
    abstract var drawable: Drawable?
    val boundPoints: FloatArray
        get() = if (!isFlipped) {
            floatArrayOf(
                0f, 0f,
                width.toFloat(), 0f,
                0f, height.toFloat(),
                width.toFloat(), height
                    .toFloat()
            )
        } else {
            floatArrayOf(
                width.toFloat(), 0f,
                0f, 0f,
                width.toFloat(), height.toFloat(),
                0f, height
                    .toFloat()
            )
        }
    val mappedBoundPoints: FloatArray
        get() {
            val dst = FloatArray(8)
            mMatrix!!.mapPoints(dst, boundPoints)
            return dst
        }

    fun getMappedPoints(src: FloatArray): FloatArray {
        val dst = FloatArray(src.size)
        mMatrix!!.mapPoints(dst, src)
        return dst
    }

    val bound: RectF
        get() = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val mappedBound: RectF
        get() {
            val dst = RectF()
            mMatrix!!.mapRect(dst, bound)
            return dst
        }
    val centerPoint: PointF
        get() = PointF((width / 2).toFloat(), (height / 2).toFloat())
    val mappedCenterPoint: PointF
        get() {
            val pointF = centerPoint
            val dst = getMappedPoints(
                floatArrayOf(
                    pointF.x,
                    pointF.y
                )
            )
            return PointF(dst[0], dst[1])
        }
    val currentScale: Float
        get() = getMatrixScale(mMatrix!!)
    val currentHeight: Float
        get() = getMatrixScale(mMatrix!!) * height
    val currentWidth: Float
        get() = getMatrixScale(mMatrix!!) * width

    /**
     * This method calculates scale value for given Matrix object.
     */
    private fun getMatrixScale(matrix: Matrix): Float {
        return sqrt(
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble().pow(2.0)
                    + getMatrixValue(
                matrix,
                Matrix.MSKEW_Y
            ).toDouble().pow(2.0)
        ).toFloat()
    }

    /**
     * @return - current image rotation angle.
     */
    val currentAngle: Float
        get() = getMatrixAngle(mMatrix!!)

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    private fun getMatrixAngle(matrix: Matrix): Float {
        return (-(atan2(
            getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(),
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble()
        ) * (180 / Math.PI))).toFloat()
    }

    private fun getMatrixValue(matrix: Matrix, @IntRange(from = 0, to = 9) valueIndex: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[valueIndex]
    }

    fun contains(x: Float, y: Float): Boolean {
        val tempMatrix = Matrix()
        tempMatrix.setRotate(-currentAngle)
        val unRotatedWrapperCorner = FloatArray(8)
        val unRotatedPoint = FloatArray(2)
        tempMatrix.mapPoints(unRotatedWrapperCorner, mappedBoundPoints)
        tempMatrix.mapPoints(unRotatedPoint, floatArrayOf(x, y))
        return trapToRect(unRotatedWrapperCorner).contains(unRotatedPoint[0], unRotatedPoint[1])
    }

    abstract fun release()

    companion object {
        protected const val TAG = "StickerPip"
    }
}