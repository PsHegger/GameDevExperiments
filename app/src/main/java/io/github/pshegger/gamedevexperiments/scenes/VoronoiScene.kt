package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.PoissonBridson
import io.github.pshegger.gamedevexperiments.algorithms.Voronoi
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene

/**
 * @author pshegger@gmail.com
 */
class VoronoiScene(val gameSurfaceView: GameSurfaceView) : Scene {
    var voronoi = Voronoi(emptyList())
    var width: Int = 0
    var height: Int = 0

    val pointPaint = Paint()

    var btnRegenerate: Button? = null

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRegenerate = Button("GEN", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { generate() }
        }

        generate()
    }

    private fun generate() {
        val poisson = PoissonBridson(margin = 5, radius = 80)
        poisson.reset(width, height)

        poisson.generateAll()
        voronoi = Voronoi(poisson.points.map { it.p })
    }

    override fun update(deltaTime: Long) {
        btnRegenerate?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        voronoi.points.forEach {
            canvas.drawCircle(it.x, it.y, 5f, pointPaint)
        }

        btnRegenerate?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }
}