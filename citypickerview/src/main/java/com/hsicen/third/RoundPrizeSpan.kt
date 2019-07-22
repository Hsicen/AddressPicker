package com.hsicen.third

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import android.util.TypedValue

/**
 * <p>作者：Hsicen  6/17/2019 5:00 PM
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：圆形Span
 */
class RoundPrizeSpan(private val bgColor: Int, val textColor: Int) : ReplacementSpan() {
    private val mRadius = dp2px(2f)
    private var mSize = 0

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {

        mSize = (paint.measureText(text, start, end) + mRadius * 2).toInt()
        return mSize
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val defColor = paint.color
        val def = paint.strokeWidth

        paint.color = bgColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp2px(1f)
        paint.isAntiAlias = true

        val mRectF = RectF(
            x + 2.5f,
            y + 2.5f + paint.ascent() - dp2px(1f),
            x + mSize - dp2px(3f),
            y + paint.descent() + dp2px(1f)
        )
        canvas.drawRoundRect(mRectF, mRadius, mRadius, paint)

        paint.color = textColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = def
        paint.textSize = sp2px(12f)
        canvas.drawText(text, start, end, x + mRadius + dp2px(3f), y.toFloat(), paint)
        paint.color = defColor
    }

    private fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return spValue * fontScale
    }

    private fun dp2px(dpValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Resources.getSystem().displayMetrics)
    }
}
