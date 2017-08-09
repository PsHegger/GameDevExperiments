package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.PoissonBridson
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.PoissonMenuScene

/**
 * @author pshegger@gmail.com
 */
class PoissonBridsonScene(val gameSurfaceView: GameSurfaceView) : Scene {
    val algo = PoissonBridson(margin = 5, radius = 45)
    var width: Int = 0
    var height: Int = 0

    val pointPaint = Paint()
    val redPointPaint = Paint().apply {
        color = Color.RED
    }
    val countPaint = Paint().apply {
        textSize = 42f
        isAntiAlias = true
        color = Color.GRAY
    }

    var btnRestart: Button? = null
    var btnInstant: Button? = null

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { algo.reset(width, height) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = {
                algo.reset(width, height)
                algo.generateAll()
            }
        }

        algo.reset(width, height)
    }

    override fun update(deltaTime: Long) {
        if (algo.canGenerateMore) {
            algo.generateNextPoint()
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        algo.points.forEach {
            val paint = if (it.active) redPointPaint else pointPaint
            canvas.drawCircle(it.p.x, it.p.y, 5f, paint)
        }

        canvas.drawText("Count: ${algo.points.size}", 10f, height - 10f, countPaint)

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = PoissonMenuScene(gameSurfaceView)
    }
}