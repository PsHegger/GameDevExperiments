package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.*
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.DungeonGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene

/**
 * @author pshegger@gmail.com
 */
class DungeonGeneratorScene(val gameSurfaceView: GameSurfaceView) : Scene {
    companion object {
        private const val SCALE_FACTOR = 20
    }

    private val generator = DungeonGenerator(DungeonGenerator.Settings(0.8f, 2, 15))
    private val paint = Paint().apply {
        strokeWidth = 1f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint().apply {
        textSize = 42f
        color = Color.GRAY
    }

    private var btnRestart: Button? = null
    private var btnInstant: Button? = null
    private var width: Int = 0
    private var height: Int = 0

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { generator.reset(width / SCALE_FACTOR, height / SCALE_FACTOR) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = {
                generator.reset(width / SCALE_FACTOR, height / SCALE_FACTOR)
                generator.generateAll()
            }
        }

        generator.reset(width / SCALE_FACTOR, height / SCALE_FACTOR)
    }

    override fun update(deltaTime: Long) {
        if (generator.canGenerateMore) {
            generator.nextStep()
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        generator.rooms.forEach { room ->
            canvas.drawRect(room.getRect(), paint)
        }

        canvas.drawText("Count: ${generator.rooms.size}", 10f, height - 10f, textPaint)

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }

    private fun DungeonGenerator.Room.getRect() = RectF(topLeft.x * SCALE_FACTOR, topLeft.y * SCALE_FACTOR, (topLeft.x + width) * SCALE_FACTOR, (topLeft.y + height) * SCALE_FACTOR)
}