package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.DelaunayGenerator
import io.github.pshegger.gamedevexperiments.algorithms.PoissonBridson
import io.github.pshegger.gamedevexperiments.algorithms.Voronoi
import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Triangle
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MapGenerationMenuScene

/**
 * @author pshegger@gmail.com
 */
class VoronoiScene(val gameSurfaceView: GameSurfaceView) : Scene {
    companion object {
        const val POISSON_RADIUS = 80
    }

    private var generator = Voronoi(emptyList())
    var width: Int = 0
    var height: Int = 0

    private val pointPaint = Paint()
    private val edgePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
    }
    private val triangleEdgePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
    }

    private var btnRestart: Button? = null
    private var btnInstant: Button? = null

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { initGenerator() }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = {
                initGenerator()
                generator.generateAll()
            }
        }

        initGenerator()
    }

    private fun initGenerator() {
        val scaledWidth = width + 2 * POISSON_RADIUS
        val scaledHeight = height + 2 * POISSON_RADIUS

        val poisson = PoissonBridson(margin = 5, radius = POISSON_RADIUS)
        poisson.reset(scaledWidth, scaledHeight)

        poisson.generateAll()
        val delaunay = DelaunayGenerator(poisson.points.map { it.p })
        delaunay.reset(scaledWidth, scaledHeight)
        delaunay.generateAll()

        generator = Voronoi(delaunay.triangles.map { it.shift(-POISSON_RADIUS) })
        generator.reset()
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

        generator.triangles.forEach {
            it.render(canvas)
        }

        generator.polygons.forEach { p ->
            p.edges.forEach { e ->
                e.render(canvas, edgePaint)
            }
        }

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MapGenerationMenuScene(gameSurfaceView)
    }

    private fun Triangle.render(canvas: Canvas, drawEdges: Boolean = false) {
        if (drawEdges) {
            edges.forEach {
                it.render(canvas, triangleEdgePaint)
            }
        }

        points.forEach {
            canvas.drawCircle(it.x, it.y, 5f, pointPaint)
        }
    }

    private fun Edge.render(canvas: Canvas, paint: Paint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
}