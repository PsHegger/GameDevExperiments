package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.*
import android.util.Log
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.DungeonGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * @author pshegger@gmail.com
 */
class DungeonGeneratorScene(val gameSurfaceView: GameSurfaceView) : Scene {
    companion object {
        private const val SCALE_FACTOR = 20
        private const val SCREEN_MARGIN = 20
    }

    private val generator = DungeonGenerator(DungeonGenerator.Settings(0.5f, 3, 10, 0.5f))
    private val paint = Paint().apply {
        strokeWidth = 2f
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
    private var scaledWidth: Int = 0
    private var scaledHeight: Int = 0

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        scaledWidth = width / SCALE_FACTOR
        scaledHeight = height / SCALE_FACTOR

        btnRestart = Button("RES", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { generator.reset(scaledWidth, scaledHeight) }
        }

        btnInstant = Button("INS", width - 400f, height - 120f, width - 240f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = {
                generator.reset(scaledWidth, scaledHeight)
                generator.generateAll()
            }
        }

        generator.reset(scaledWidth, scaledHeight)
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

        val scaleFactor = calculateScale()

        generator.rooms.forEach { roomState ->
            paint.color = when (roomState.state) {
                DungeonGenerator.RoomState.State.Generated -> Color.DKGRAY
                DungeonGenerator.RoomState.State.Placed -> Color.BLUE
                DungeonGenerator.RoomState.State.Selected -> Color.RED
            }

            canvas.drawRect(roomState.room.getRect(scaleFactor), paint)
        }

        canvas.drawText("Count: ${generator.rooms.size}", 10f, height - 10f, textPaint)

        btnRestart?.render(canvas)
        btnInstant?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }

    private fun calculateScale(): Float {
        val distances = generator.rooms.map { room ->
            val left = room.room.topLeft.x - scaledWidth / 2f
            val top = room.room.topLeft.y - scaledHeight / 2f

            Pair(
                    Pair(left.absoluteValue, (left + room.room.width).absoluteValue),
                    Pair(top.absoluteValue, (top + room.room.height).absoluteValue)
            )
        }
        val horizontalScale = distances.flatMap { d ->
            listOf(d.first.first, d.first.second)
        }.max()?.let {
            (width - 2 * SCREEN_MARGIN) / (it * 2)
        } ?: SCALE_FACTOR.toFloat()
        val verticalScale = distances.flatMap { d ->
            listOf(d.second.first, d.second.second)
        }.max()?.let {
            (height - 2 * SCREEN_MARGIN) / (it * 2)
        } ?: SCALE_FACTOR.toFloat()

        return listOf(verticalScale, horizontalScale, SCALE_FACTOR.toFloat()).min()!!
    }

    private fun DungeonGenerator.Room.getRect(scaleFactor: Float): RectF {
        val left = (topLeft.x - scaledWidth / 2f) * scaleFactor + this@DungeonGeneratorScene.width / 2f
        val top = (topLeft.y - scaledHeight / 2f) * scaleFactor + this@DungeonGeneratorScene.height / 2f
        val right = left + width * scaleFactor
        val bottom = top + height * scaleFactor

        return RectF(left, top, right, bottom)
    }
}