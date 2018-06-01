package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.maze.BaseMazeGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MazeMenuScene

/**
 * @author pshegger@gmail.com
 */
class MazeScene(val gameSurfaceView: GameSurfaceView, private val generator: BaseMazeGenerator) : Scene {
    private val cellSize = 20
    private val margin = 10

    private var width = 0
    private var height = 0

    val cellPaint = Paint()

    var btnRestart: Button? = null
    var btnInstant: Button? = null

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        val cellCountX = (width - 2 * margin) / cellSize
        val cellCountY = (height - 2 * margin) / cellSize

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.argb(150, 0, 0, 0), Color.LTGRAY, Color.MAGENTA, 50f).apply {
            onClick = { generator.reset(cellCountX, cellCountY) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.argb(150, 0, 0, 0), Color.LTGRAY, Color.MAGENTA, 50f).apply {
            onClick = {
                generator.reset(cellCountX, cellCountY)
                generator.generateAll()
            }
        }

        generator.reset(cellCountX, cellCountY)
    }

    override fun update(deltaTime: Long) {
        if (!generator.finished) {
            generator.nextStep()
        }

        btnRestart?.update(deltaTime, gameSurfaceView.touch)
        btnInstant?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        cellPaint.color = Color.rgb(154, 206, 235)
        canvas.drawRect(margin.toFloat(), margin.toFloat(), width - margin.toFloat(), height - margin.toFloat(), cellPaint)

        generator.fields.forEachIndexed { y, row ->
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

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MazeMenuScene(gameSurfaceView)
    }

    override fun fpsColor(): Int = Color.MAGENTA
}