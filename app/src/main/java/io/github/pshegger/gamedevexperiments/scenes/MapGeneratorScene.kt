package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.MapGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MapGenerationMenuScene
import io.github.pshegger.gamedevexperiments.utils.timeLimitedWhile
import io.github.pshegger.gamedevexperiments.utils.toLinesArray
import io.github.pshegger.gamedevexperiments.utils.toPointsArray

/**
 * @author pshegger@gmail.com
 */
class MapGeneratorScene(val gameSurfaceView: GameSurfaceView) : Scene {
    private var generator = MapGenerator()
    var width: Int = 0
    var height: Int = 0

    private var btnRestart: Button? = null
    private var btnInstant: Button? = null

    private val pointPaint = Paint().apply {
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
        isAntiAlias = true
    }
    private val delaunayEdgePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 1f
        isAntiAlias = true
    }
    private val voronoiEdgePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        isAntiAlias = true
    }

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { generator.reset(width, height) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = {
                generator.reset(width, height)
                generator.generateAll()
            }
        }

        generator.reset(width, height)
    }

    override fun update(deltaTime: Long) {
        if (generator.canGenerateMore) {
            timeLimitedWhile(5L, { generator.canGenerateMore }) {
                generator.generateNext()
            }
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        when (generator.state) {
            MapGenerator.State.Poisson -> renderPoisson(canvas)
            MapGenerator.State.Delaunay -> renderDelaunay(canvas)
            MapGenerator.State.Voronoi -> renderVoronoi(canvas, true)
            MapGenerator.State.Finished -> renderVoronoi(canvas, false)
        }

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    private fun renderPoisson(canvas: Canvas) {
        pointPaint.color = Color.BLACK
        canvas.drawPoints(generator.poissonPoints.filterNot { it.active }.map { it.p }.toPointsArray(), pointPaint)
        pointPaint.color = Color.RED
        canvas.drawPoints(generator.poissonPoints.filter { it.active }.map { it.p }.toPointsArray(), pointPaint)
    }

    private fun renderDelaunay(canvas: Canvas) {
        canvas.drawLines(generator.delaunayEdges.toLinesArray(), delaunayEdgePaint)

        pointPaint.color = Color.BLACK
        canvas.drawPoints(generator.delaunayPoints.toPointsArray(), pointPaint)
    }

    private fun renderVoronoi(canvas: Canvas, drawDelaunayEdges: Boolean) {
        if (drawDelaunayEdges) {
            canvas.drawLines(generator.delaunayEdges.toLinesArray(), delaunayEdgePaint)
        }

        canvas.drawLines(generator.voronoiEdges.toLinesArray(), voronoiEdgePaint)

        pointPaint.color = Color.BLACK
        canvas.drawPoints(generator.voronoiPoints.filterNot { it.isActive }.map { it.p }.toPointsArray(), pointPaint)
        pointPaint.color = Color.RED
        canvas.drawPoints(generator.voronoiPoints.filter { it.isActive }.map { it.p }.toPointsArray(), pointPaint)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MapGenerationMenuScene(gameSurfaceView)
    }
}