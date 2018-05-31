package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.MapGenerator
import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Vector
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MapGenerationMenuScene

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
            generator.generateNext()
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
        generator.poissonPoints.forEach {
            pointPaint.color = if (it.active) Color.RED else Color.BLACK
            it.p.render(canvas, pointPaint)
        }
    }

    private fun renderDelaunay(canvas: Canvas) {
        generator.delaunayEdges.forEach {
            canvas.drawLine(it.start.x, it.start.y, it.end.x, it.end.y, delaunayEdgePaint)
        }

        generator.delaunayPoints.forEach {
            canvas.drawCircle(it.x, it.y, 5f, pointPaint)
        }
    }

    private fun renderVoronoi(canvas: Canvas, drawDelaunayEdges: Boolean) {
        if (drawDelaunayEdges) {
            generator.delaunayEdges.forEach {
                canvas.drawLine(it.start.x, it.start.y, it.end.x, it.end.y, delaunayEdgePaint)
            }
        }

        generator.voronoiEdges.forEach { it.render(canvas, voronoiEdgePaint) }

        generator.voronoiPoints.forEach {
            pointPaint.color = if (it.isActive) Color.RED else Color.BLACK
            canvas.drawCircle(it.p.x, it.p.y, 5f, pointPaint)
        }
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MapGenerationMenuScene(gameSurfaceView)
    }

    private fun Vector.render(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(x, y, 5f, paint)
    }

    private fun Edge.render(canvas: Canvas, paint: Paint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
}