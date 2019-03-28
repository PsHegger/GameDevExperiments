package io.github.pshegger.gamedevexperiments.scenes.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.simplex.SimplexNoiseGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MapGenerationMenuScene
import io.github.pshegger.gamedevexperiments.utils.timeLimitedWhile

/**
 * @author pshegger@gmail.com
 */
class SimplexGeneratorScene(val gameSurfaceView: GameSurfaceView) : Scene {
    private var generator = SimplexNoiseGenerator()
    private var width: Int = 0
    private var height: Int = 0

    private var btnRestart: Button? = null
    private var btnInstant: Button? = null

    private val bmpPaint = Paint()

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.argb(150, 0, 0, 0), Color.MAGENTA, Color.MAGENTA, 50f).apply {
            setOnClickListener { generator.reset(width, height) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.argb(150, 0, 0, 0), Color.MAGENTA, Color.MAGENTA, 50f).apply {
            setOnClickListener {
                generator.reset(width, height)
                generator.generateAll()
            }
        }

        generator.reset(width, height)
    }

    override fun update(deltaTime: Long) {
        if (generator.canGenerateMore) {
            timeLimitedWhile(15.0, {generator.canGenerateMore}) {
                generator.generateNextPoint()
            }
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        canvas.drawBitmap(generator.bitmap, 0f, 0f, bmpPaint)

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MapGenerationMenuScene(gameSurfaceView)
    }
}
