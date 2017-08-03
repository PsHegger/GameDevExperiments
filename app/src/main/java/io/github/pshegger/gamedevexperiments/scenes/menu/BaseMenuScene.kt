package io.github.pshegger.gamedevexperiments.scenes.menu

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.hud.Button

/**
 * @author gergely.hegedus@tappointment.com
 */
abstract class BaseMenuScene(val gameSurfaceView: GameSurfaceView) : Scene {
    private var width: Int = 0
    private var height: Int = 0

    private val margin = 20f
    private var buttons = listOf<Button>()

    protected abstract val scenes: List<MenuItem>
    protected abstract val title: String

    private val titlePaint = Paint().apply {
        textSize = 96f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        color = Color.DKGRAY
    }

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        val textS = 80f
        val textPaint = Paint().apply {
            textSize = textS
            textAlign = Paint.Align.CENTER
        }

        val textHeight = -(textPaint.descent() + textPaint.ascent())
        val rectHeight = textHeight * 2.5f

        val titleTextHeight = -(titlePaint.descent() + titlePaint.ascent())
        val titleHeight = titleTextHeight * 1.5f

        buttons = scenes.mapIndexed { i, (title, scene) ->
            val top = (i + 4) * margin + i * rectHeight + titleHeight
            Button(title, margin, top, width - margin, top + rectHeight, Color.GREEN, Color.RED, Color.BLACK, textS).apply {
                onClick = { gameSurfaceView.scene = scene }
            }
        }
    }

    override fun update(deltaTime: Long) {
        buttons.forEach { it.update(deltaTime, gameSurfaceView.touch) }
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))
        canvas.drawText(title, width / 2f, 96f, titlePaint)

        buttons.forEach { it.render(canvas) }
    }

    protected data class MenuItem(val title: String, val scene: Scene)
}