package com.example.pharmashield

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GraphicOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()
    private var previewWidth: Int = 0
    private var previewHeight: Int = 0
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f
    private var textRects = mutableListOf<Rect>()

    private val rectPaint = Paint().apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    /** Updates frame dimensions received from CameraX analyzer */
    fun setPreviewSize(width: Int, height: Int) {
        synchronized(lock) {
            previewWidth = width
            previewHeight = height
        }
    }

    /** Clears old bounding boxes and passes updated list for rendering */
    fun updateTextRects(rects: List<Rect>) {
        synchronized(lock) {
            textRects.clear()
            textRects.addAll(rects)
        }
        postInvalidate() // Requests redrawing on the UI thread
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth == 0 || previewHeight == 0) return

            // CameraX sends portrait frames rotated 90 degrees relative to screen
            val isPortrait = height > width
            val processWidth = if (isPortrait) previewHeight else previewWidth
            val processHeight = if (isPortrait) previewWidth else previewHeight

            scaleX = width.toFloat() / processWidth.toFloat()
            scaleY = height.toFloat() / processHeight.toFloat()

            for (rect in textRects) {
                val mappedRect = if (isPortrait) {
                    // Coordinate transformation for portrait rotation
                    RectF(
                        width - (rect.bottom * scaleX),
                        rect.left * scaleY,
                        width - (rect.top * scaleX),
                        rect.right * scaleY
                    )
                } else {
                    RectF(
                        rect.left * scaleX,
                        rect.top * scaleY,
                        rect.right * scaleX,
                        rect.bottom * scaleY
                    )
                }
                canvas.drawRect(mappedRect, rectPaint)
            }
        }
    }
}