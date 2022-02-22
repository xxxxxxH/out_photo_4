package com.edg.stickerlibrary.view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.sqrt

class CustomImageView : AppCompatImageView {
    var imageBitmap: Bitmap? = null
        private set
    private var containerWidth = 0
    private var containerHeight = 0
    var background: Paint? = null

    //Matrices will be used to move and zoom image
    var savedMatrix = Matrix()
    var start = PointF()
    var currentScale = 0f
    var curX = 0f
    var curY = 0f
    var mode = NONE

    //For animating stuff
    var targetX = 0f
    var targetY = 0f
    var targetScale = 0f
    var targetScaleX = 0f
    var targetScaleY = 0f
    var scaleChange = 0f
    var targetRatio = 0f
    var transitionalRatio = 0f
    var easing = 0.2f
    var isAnimating = false
    var scaleDampingFactor = 0.5f

    //For pinch and zoom
    var oldDist = 1f
    var mid = PointF()
    private val mHandler = Handler()
    var minScale = 0f
    var maxScale = 8.0f
    var wpRadius = 25.0f
    var wpInnerRadius = 20.0f
    var screenDensity: Float
    var mContext: Context? = null
    private var gestureDetector: GestureDetector
    private var defaultScale = 0

    constructor(mContext: Context) : super(mContext) {
        isFocusable = true
        isFocusableInTouchMode = true
        screenDensity = mContext.resources.displayMetrics.density
        initPaints()
        gestureDetector = GestureDetector(MyGestureDetector())
        this.mContext = mContext
    }

    constructor(mContext: Context, attrs: AttributeSet?) : super(mContext, attrs) {
        screenDensity = mContext.resources.displayMetrics.density
        initPaints()
        gestureDetector = GestureDetector(MyGestureDetector())
        defaultScale = DEFAULT_SCALE_FIT_INSIDE
    }

    private fun initPaints() {
        background = Paint()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        //Reset the width and height. Will draw bitmap and change
        containerWidth = width
        containerHeight = height
        if (imageBitmap != null) {
            val imgHeight = imageBitmap!!.height
            val imgWidth = imageBitmap!!.width
            val scale: Float
            var initX = 0
            var initY = 0
            if (defaultScale == DEFAULT_SCALE_FIT_INSIDE) {
                if (imgWidth > containerWidth) {
                    scale = containerWidth.toFloat() / imgWidth
                    val newHeight = imgHeight * scale
                    initY = (containerHeight - newHeight.toInt()) / 2
                    matrix.setScale(scale, scale)
                    matrix.postTranslate(0f, initY.toFloat())
                } else {
                    scale = containerHeight.toFloat() / imgHeight
                    val newWidth = imgWidth * scale
                    initX = (containerWidth - newWidth.toInt()) / 2
                    matrix.setScale(scale, scale)
                    matrix.postTranslate(initX.toFloat(), 0f)
                }
                curX = initX.toFloat()
                curY = initY.toFloat()
                currentScale = scale
                minScale = scale
            } else {
                if (imgWidth > containerWidth) {
                    initY = (containerHeight - imgHeight) / 2
                    matrix.postTranslate(0f, initY.toFloat())
                } else {
                    initX = (containerWidth - imgWidth) / 2
                    matrix.postTranslate(initX.toFloat(), 0f)
                }
                curX = initX.toFloat()
                curY = initY.toFloat()
                currentScale = 1.0f
                minScale = 1.0f
            }
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (imageBitmap != null) {
            canvas.drawBitmap(imageBitmap!!, matrix, background)
        }
    }

    //Checks and sets the target image x and y co-ordinates if out of bounds
    private fun checkImageConstraints() {
        Log.i("zoom", "checkImageConstraints")
        if (imageBitmap == null) {
            return
        }
        val mValues = FloatArray(9)
        matrix.getValues(mValues)
        currentScale = mValues[0]

        matrix.getValues(mValues)
        currentScale = mValues[0]
        curX = mValues[2]
        curY = mValues[5]
        val rangeLimitX = containerWidth - (imageBitmap!!.width * currentScale).toInt()
        val rangeLimitY = containerHeight - (imageBitmap!!.height * currentScale).toInt()
        val toMoveX = false
        val toMoveY = false
        if (rangeLimitX < 0) {
            if (curX > 0) {
                targetX = 0f
            } else if (curX < rangeLimitX) {
                targetX = rangeLimitX.toFloat()
            }
        } else {
            targetX = (rangeLimitX / 2).toFloat()
        }
        if (rangeLimitY < 0) {
            if (curY > 0) {
                targetY = 0f
            } else if (curY < rangeLimitY) {
                targetY = rangeLimitY.toFloat()
            }
        } else {
            targetY = (rangeLimitY / 2).toFloat()
        }
        if (toMoveX || toMoveY) {
            Log.i("zoom", "checkImageConstraints if")
            if (!toMoveY) {
                targetY = curY
            }
            if (!toMoveX) {
                targetX = curX
            }

            //Disable touch event actions
            isAnimating = true
            //Initialize timer
            mHandler.removeCallbacks(mUpdateImagePositionTask)
            mHandler.postDelayed(mUpdateImagePositionTask, 100)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var oldX = 0f
        var newX = 0f
        val sens = 5f
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }
        if (isAnimating) {
            return true
        }

        //Handle touch events here
        val mValues = FloatArray(9)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.i("Image", "ACTION_DOWN")

                if (!isAnimating) {
                    savedMatrix.set(matrix)
                    oldX = event.x
                    start[event.x] = event.y
                    mode = DRAG
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i("Image", "ACTION_POINTER_DOWN")
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.i("Image", "ACTION_UP")

                newX = event.x
                if (abs(oldX - newX) < sens) {
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.i("Image", "ACTION_POINTER_UP")
                mode = NONE
                matrix.getValues(mValues)
                curX = mValues[2]
                curY = mValues[5]
                currentScale = mValues[0]
                if (!isAnimating) {
                    checkImageConstraints()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Log.i("Image", "ACTION_MOVE")
                if (mode == DRAG && !isAnimating) {
                    matrix.set(savedMatrix)
                    val diffX = event.x - start.x
                    val diffY = event.y - start.y
                    matrix.postTranslate(diffX, diffY)
                    matrix.getValues(mValues)
                    curX = mValues[2]
                    curY = mValues[5]
                    currentScale = mValues[0]
                } else if (mode == ZOOM && !isAnimating) {
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        matrix.set(savedMatrix)
                        val scale = newDist / oldDist
                        matrix.getValues(mValues)
                        currentScale = mValues[0]
                        when {
                            currentScale * scale <= minScale -> {
                                Log.i("zoom", "if")
                                matrix.postScale(scale, scale, mid.x, mid.y)
                            }
                            currentScale * scale >= maxScale -> {
                                Log.i("zoom", "else if")
                                matrix.postScale(scale, scale, mid.x, mid.y)
                            }
                            else -> {
                                Log.i("zoom", "else")
                                matrix.postScale(scale, scale, mid.x, mid.y)
                            }
                        }
                        matrix.getValues(mValues)
                        curX = mValues[2]
                        curY = mValues[5]
                        currentScale = mValues[0]
                    }
                }
            }
        }

        invalidate()
        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    override fun setImageBitmap(b: Bitmap) {
        imageBitmap = b
        containerWidth = width
        containerHeight = height
        val imgHeight = imageBitmap!!.height
        val imgWidth = imageBitmap!!.width
        val scale: Float
        var initX = 0
        var initY = 0
        matrix.reset()
        if (defaultScale == DEFAULT_SCALE_FIT_INSIDE) {
            if (imgWidth > containerWidth) {
                scale = containerWidth.toFloat() / imgWidth
                val newHeight = imgHeight * scale
                initY = (containerHeight - newHeight.toInt()) / 2
                matrix.setScale(scale, scale)
                matrix.postTranslate(0f, initY.toFloat())
            } else {
                scale = containerHeight.toFloat() / imgHeight
                val newWidth = imgWidth * scale
                initX = (containerWidth - newWidth.toInt()) / 2
                matrix.setScale(scale, scale)
                matrix.postTranslate(initX.toFloat(), 0f)
            }
            curX = initX.toFloat()
            curY = initY.toFloat()
            currentScale = scale
            minScale = scale
        } else {
            if (imgWidth > containerWidth) {
                initX = 0
                initY = if (imgHeight > containerHeight) {
                    0
                } else {
                    (containerHeight - imgHeight) / 2
                }
                matrix.postTranslate(0f, initY.toFloat())
            } else {
                initX = (containerWidth - imgWidth) / 2
                initY = if (imgHeight > containerHeight) {
                    0
                } else {
                    (containerHeight - imgHeight) / 2
                }
                matrix.postTranslate(initX.toFloat(), 0f)
            }
            curX = initX.toFloat()
            curY = initY.toFloat()
            currentScale = 1.0f
            minScale = 1.0f
        }
        invalidate()
    }

    private val mUpdateImagePositionTask: Runnable = object : Runnable {
        override fun run() {
            val mValues: FloatArray
            if (abs(targetX - curX) < 5 && abs(targetY - curY) < 5) {
                Log.i("zoom", "update pos if")
                isAnimating = false
                mHandler.removeCallbacks(this)
                mValues = FloatArray(9)
                matrix.getValues(mValues)
                currentScale = mValues[0]
                curX = mValues[2]
                curY = mValues[5]

                val diffX = targetX - curX
                val diffY = targetY - curY
                matrix.postTranslate(diffX, diffY)
            } else {
                Log.i("zoom", "update pos else")
                isAnimating = true
                mValues = FloatArray(9)
                matrix.getValues(mValues)
                currentScale = mValues[0]
                curX = mValues[2]
                curY = mValues[5]

                val diffX = (targetX - curX) * 0.3f
                val diffY = (targetY - curY) * 0.3f
                matrix.postTranslate(diffX, diffY)
                mHandler.postDelayed(this, 25)
            }
            invalidate()
        }
    }
    private val mUpdateImageScale: Runnable = object : Runnable {
        override fun run() {
            val transitionalRatio = targetScale / currentScale
            val dx: Float
            if (abs(transitionalRatio - 1) > 0.05) {
                isAnimating = true
                if (targetScale > currentScale) {
                    dx = transitionalRatio - 1
                    scaleChange = 1 + dx * 0.2f
                    currentScale *= scaleChange
                    if (currentScale > targetScale) {
                        currentScale /= scaleChange
                        scaleChange = 1f
                    }
                } else {
                    dx = 1 - transitionalRatio
                    scaleChange = 1 - dx * 0.5f
                    currentScale *= scaleChange
                    if (currentScale < targetScale) {
                        currentScale /= scaleChange
                        scaleChange = 1f
                    }
                }
                if (scaleChange != 1f) {
                    matrix.postScale(scaleChange, scaleChange, targetScaleX, targetScaleY)
                    mHandler.postDelayed(this, 15)
                    invalidate()
                } else {
                    isAnimating = false
                    scaleChange = 1f
                    matrix.postScale(
                        targetScale / currentScale,
                        targetScale / currentScale,
                        targetScaleX,
                        targetScaleY
                    )
                    currentScale = targetScale
                    mHandler.removeCallbacks(this)
                    invalidate()
                    checkImageConstraints()
                }
            } else {
                isAnimating = false
                scaleChange = 1f
                matrix.postScale(
                    targetScale / currentScale,
                    targetScale / currentScale,
                    targetScaleX,
                    targetScaleY
                )
                currentScale = targetScale
                mHandler.removeCallbacks(this)
                invalidate()
                checkImageConstraints()
            }
        }
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf(
            "DOWN",
            "UP",
            "MOVE",
            "CANCEL",
            "OUTSIDE",
            "POINTER_DOWN",
            "POINTER_UP",
            "7?",
            "8?",
            "9?"
        )
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount) sb.append(";")
        }
        sb.append("]")
    }

    internal inner class MyGestureDetector : SimpleOnGestureListener() {
        override fun onDoubleTap(event: MotionEvent): Boolean {
            if (isAnimating) {
                return true
            }
            scaleChange = 1f
            isAnimating = true
            targetScaleX = event.x
            targetScaleY = event.y
            targetScale = if (abs(currentScale - maxScale) > 0.1) {
                maxScale
            } else {
                minScale
            }
            targetRatio = targetScale / currentScale
            mHandler.removeCallbacks(mUpdateImageScale)
            mHandler.post(mUpdateImageScale)
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onDown(e: MotionEvent): Boolean {
            return false
        }
    }

    override fun destroyDrawingCache() {
        super.destroyDrawingCache()
    }

    companion object {
        //We can be in one of these 3 states
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val DEFAULT_SCALE_FIT_INSIDE = 0
        const val DEFAULT_SCALE_ORIGINAL = 1
    }
}