package io.github.pshegger.gamedevexperiments.scenes.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.DelaunayGenerator
import io.github.pshegger.gamedevexperiments.algorithms.PoissonBridson
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MapGenerationMenuScene
import io.github.pshegger.gamedevexperiments.utils.toLinesArray
import io.github.pshegger.gamedevexperiments.utils.toPointsArray

/**
 * @author pshegger@gmail.com
 */
class DelaunayBuildingScene(val gameSurfaceView: GameSurfaceView) : Scene {
    private var generator = DelaunayGenerator(emptyList())
    var width: Int = 0
    var height: Int = 0

    private val pointPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
        isAntiAlias = true
    }
    private val edgePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private var btnRestart: Button? = null
    private var btnInstant: Button? = null

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f).apply {
            setOnClickListener { initGenerator() }
        }

        btnInstant = Button("INS", width - 400f, height - 120f).apply {
            setOnClickListener {
                initGenerator()
                generator.generateAll()
            }
        }

        initGenerator()
    }

    private fun initGenerator() {
        val poisson = PoissonBridson(margin = 5, radius = 80)
        poisson.reset(width, height)

        poisson.generateAll()
        generator = DelaunayGenerator(poisson.points.map { it.p })
        generator.reset(width, height)
    }

    override fun update(deltaTime: Long) {
        if (generator.canGenerateMore) {
            generator.generateNextEdge()
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        canvas.drawLines(generator.edges.toLinesArray(), edgePaint)
        canvas.drawPoints(generator.points.toPointsArray(), pointPaint)

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MapGenerationMenuScene(gameSurfaceView)
    }
}
