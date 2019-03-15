package com.rzahr.quicktools

import android.graphics.*
import android.graphics.drawable.Drawable

class QuickBadgeDrawable constructor(backgroundColor: Int, textColor: Int, textSize: Float = 14f): Drawable() {

    var mBadgePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mTxtRect = Rect()
    private var mCount = ""
    private var mWillDraw = false

    init  {

        //FOR THE CIRCLE
        mBadgePaint = Paint()
        mBadgePaint!!.color = backgroundColor
        mBadgePaint!!.isAntiAlias = true
        mBadgePaint!!.style = Paint.Style.FILL

        //FOR THE NUMBER WITHIN THE CIRCLE
        mTextPaint = Paint()
        mTextPaint!!.color = textColor
        mTextPaint!!.typeface = Typeface.DEFAULT_BOLD
        mTextPaint!!.textSize =  textSize
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (!mWillDraw) return

        //CREATING THE CIRCLE SHAPE
        val bounds = bounds
        //float width = bounds.right - bounds.left;
        //float height = bounds.bottom - bounds.top;
        val width = (bounds.right - bounds.left + 20).toFloat()
        val height = (bounds.bottom - bounds.top + 20).toFloat()

        // Position the badge in the top-right quadrant of the icon.
        val radius = (Math.min(width, height) / 2 - 1) / 1.75f
        val centerX = width - radius + 2f
        val centerY = radius - 4

        // Draw badge circle.
        canvas.drawCircle(centerX, centerY, radius, mBadgePaint!!)

        // Draw badge count text inside the circle.
        mTextPaint?.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = (mTxtRect.bottom - mTxtRect.top).toFloat()
        val textY = centerY + textHeight / 2f
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: String) {
        mCount = count

        // Only draw a badge if there are notifications.
        mWillDraw = count.contains("+") || Integer.parseInt(count) > 0
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}