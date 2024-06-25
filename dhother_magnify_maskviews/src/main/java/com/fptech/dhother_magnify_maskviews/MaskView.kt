package com.fptech.objectremoverapp.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs

class MaskView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val maskPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var maskBitmap: Bitmap
    private lateinit var maskCanvas: Canvas
    private val pathsHistory = mutableListOf<Path>()
    private var currentPath: Path? = null
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private val undonePaths = mutableListOf<Path>()
    private var isEraserMode: Boolean = false

    var strokeWidth: Float = 50f
        set(value) {
            field = value
            maskPaint.strokeWidth = value
        }

    init {
        setupMaskPaint()
        setupMaskBitmap()
    }

    private fun setupMaskPaint() {
        maskPaint.color = Color.WHITE
        maskPaint.style = Paint.Style.STROKE
        maskPaint.strokeWidth = strokeWidth
        maskPaint.strokeCap = Paint.Cap.ROUND
    }

    private fun setupMaskBitmap() {
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        maskCanvas = Canvas(maskBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawBitmap(maskBitmap, 0f, 0f, null)
    }

    fun setTouch(event: MotionEvent, viewGroup: ViewGroup): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startNewPath()
                currentPath?.moveTo(x, y)
                lastTouchX = x
                lastTouchY = y
                drawMask(x, y)
                maskCanvas.drawPoint(x, y, maskPaint)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(x - lastTouchX)
                val dy = abs(y - lastTouchY)
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    currentPath?.quadTo(
                        lastTouchX,
                        lastTouchY,
                        (x + lastTouchX) / 2,
                        (y + lastTouchY) / 2
                    )
                    lastTouchX = x
                    lastTouchY = y
                    drawMask(x, y)
                    maskCanvas.drawPath(currentPath!!, maskPaint)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                saveCurrentPath()
            }
        }

        return true
    }

    fun undo() {
        if (pathsHistory.isNotEmpty()) {
            undonePaths.add(pathsHistory.removeAt(pathsHistory.size - 1))
            redrawPaths()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            pathsHistory.add(undonePaths.removeAt(undonePaths.size - 1))
            redrawPaths()
        }
    }

    fun clearAllPaths() {
        pathsHistory.clear()
        undonePaths.clear()
        maskBitmap.eraseColor(Color.TRANSPARENT)
        invalidate()
    }

    fun setEraserMode(isEraser: Boolean) {
        isEraserMode = isEraser
        if (isEraserMode) {
            maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        } else {
            maskPaint.xfermode = null
        }
    }

    private fun drawMask(x: Float, y: Float) {
        maskCanvas.drawPoint(x, y, maskPaint)
        invalidate()
    }

    private fun redrawPaths() {
        maskBitmap.eraseColor(Color.TRANSPARENT)
        for (path in pathsHistory) {
            maskCanvas.drawPath(path, maskPaint)
        }
        invalidate()
    }

    private fun startNewPath() {
        currentPath = Path()
    }

    private fun saveCurrentPath() {
        currentPath?.let {
            pathsHistory.add(it)
            currentPath = null
        }
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }
}
