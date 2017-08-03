package io.github.pshegger.gamedevexperiments.hud

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import io.github.pshegger.gamedevexperiments.utils.Touch

/**
 * @author gergely.hegedus@tappointment.com
 */
class Button(val text: String, left: Float, top: Float, right: Float, bottom: Float, val bgColor: Int, val borderColor: Int, val textColor: Int, val textSize: Float) : HudElement {
    var onClick: () -> Unit = {}

    val textPaint: Paint = Paint().apply {
        isAntiAlias = true
        textSize = this@Button.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = textColor
    }

    val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = 3f
    }

    val bgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = bgColor
    }

    val rect = RectF(left, top, right, bottom)

    val width = right - left
    val height = bottom - top

    var pressed = false

    override fun update(deltaTime: Long, touch: Touch?) {
        if (touch != null && rect.contains(touch.x, touch.y)) {
            pressed = true
        } else if (pressed) {
            onClick()
            pressed = false
        }
    }

    override fun render(canvas: Canvas) {
        val textHeight = -(textPaint.descent() + textPaint.ascent())

        canvas.drawRoundRect(rect, 15f, 15f, bgPaint)
        canvas.drawRoundRect(rect, 15f, 15f, borderPaint)
        canvas.drawText(text, rect.left + width / 2f, rect.top + (height + textHeight) / 2f, textPaint)
    }
}