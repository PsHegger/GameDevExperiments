package io.github.pshegger.gamedevexperiments.scenes.maze

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.maze.BaseMazeGenerator
import io.github.pshegger.gamedevexperiments.algorithms.maze.RandomDepthFirstGenerator
import io.github.pshegger.gamedevexperiments.algorithms.pathfinding.AStar
import io.github.pshegger.gamedevexperiments.algorithms.pathfinding.BasePathFinder
import io.github.pshegger.gamedevexperiments.algorithms.pathfinding.BreadthFirstSearch
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MazeMenuScene

/**
 * @author pshegger@gmail.com
 */
class PathFindingScene(val gameSurfaceView: GameSurfaceView) : Scene {
    private val btnBgColor = Color.argb(150, 0, 0, 0)
    private val btnBorderColor = Color.LTGRAY
    private val btnTextColor = Color.MAGENTA

    private val manhattanHeuristic: (BasePathFinder.Coordinate, BasePathFinder.Coordinate) -> Float = { c, stop ->
        (Math.abs(stop.x - c.x) + Math.abs(stop.y - c.y)).toFloat()
    }
    private val constantHeuristic: (BasePathFinder.Coordinate, BasePathFinder.Coordinate) -> Float = { _, _ -> 0F }
    private val euclideanHeuristic: (BasePathFinder.Coordinate, BasePathFinder.Coordinate) -> Float = { c, stop ->
        val dx = stop.x - c.x
        val dy = stop.y - c.y

        Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
    }

    private val mazeGenerator: BaseMazeGenerator = RandomDepthFirstGenerator()

    private val cellSize = 20
    private val margin = 10

    private var width = 0
    private var height = 0

    val cellPaint = Paint()

    var pathFinder: BasePathFinder? = null

    val btns: ArrayList<Button> = arrayListOf()

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        val cellCountX = (width - 2 * margin) / cellSize
        val cellCountY = (height - 2 * margin) / cellSize

        btns.clear()
        btns.add(Button("DFS", width - 200f, height - 120f, width - 40f, height - 40f, btnBgColor, btnBorderColor, btnTextColor, 50f).apply {
            onClick = { pathFinder = BreadthFirstSearch(mazeGenerator.fields) }
        })

        btns.add(Button("A*M", width - 400f, height - 120f, width - 240f, height - 40f, btnBgColor, btnBorderColor, btnTextColor, 50f).apply {
            onClick = { pathFinder = AStar(mazeGenerator.fields, manhattanHeuristic) }
        })

        btns.add(Button("A*E", width - 600f, height - 120f, width - 440f, height - 40f, btnBgColor, btnBorderColor, btnTextColor, 50f).apply {
            onClick = { pathFinder = AStar(mazeGenerator.fields, euclideanHeuristic) }
        })

        btns.add(Button("DIJ", width - 800f, height - 120f, width - 640f, height - 40f, btnBgColor, btnBorderColor, btnTextColor, 50f).apply {
            onClick = { pathFinder = AStar(mazeGenerator.fields, constantHeuristic) }
        })

        btns.add(Button("NEW", width - 200f, 40f, width - 40f, 120f, btnBgColor, btnBorderColor, btnTextColor, 50f).apply {
            onClick = {
                pathFinder = null
                mazeGenerator.reset(cellCountX, cellCountY)
                mazeGenerator.generateAll()
            }
        })

        mazeGenerator.reset(cellCountX, cellCountY)
        mazeGenerator.generateAll()
    }

    override fun update(deltaTime: Long) {
        pathFinder?.let { pathFinder ->
            if (!pathFinder.finished) {
                pathFinder.nextStep()
            }
        }

        btns.forEach { it.update(deltaTime, gameSurfaceView.touch) }
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        cellPaint.color = Color.rgb(154, 206, 235)
        canvas.drawRect(margin.toFloat(), margin.toFloat(), width - margin.toFloat(), height - margin.toFloat(), cellPaint)

        mazeGenerator.fields.forEachIndexed { y, row ->
            row.forEachIndexed { x, fieldValue ->
                cellPaint.color = when (fieldValue) {
                    BaseMazeGenerator.FieldValue.Empty -> Color.TRANSPARENT
                    BaseMazeGenerator.FieldValue.Wall -> Color.BLACK
                    BaseMazeGenerator.FieldValue.Active -> Color.RED
                    BaseMazeGenerator.FieldValue.NotProcessed -> Color.DKGRAY
                }

                val left = x * cellSize.toFloat() + margin
                val top = y * cellSize.toFloat() + margin
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint)
            }
        }

        pathFinder?.let { pathFinder ->
            pathFinder.states.forEach { state ->
                cellPaint.color = when (state.state) {
                    BasePathFinder.FieldState.FieldValue.StartStop -> Color.GREEN
                    BasePathFinder.FieldState.FieldValue.Active -> Color.RED
                    BasePathFinder.FieldState.FieldValue.Path -> Color.BLUE
                }

                val left = state.x * cellSize.toFloat() + margin
                val top = state.y * cellSize.toFloat() + margin
                canvas.drawRect(left, top, left + cellSize, top + cellSize, cellPaint)
            }
        }

        btns.forEach { it.render(canvas) }
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MazeMenuScene(gameSurfaceView)
    }

    override fun fpsColor(): Int = Color.MAGENTA
}