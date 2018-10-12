package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.*
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.algorithms.DungeonGenerator
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene

/**
 * @author pshegger@gmail.com
 */
class DungeonGeneratorScene(val gameSurfaceView: GameSurfaceView) : Scene {
    companion object {
        private const val SCALE_FACTOR = 20
        private const val SCREEN_MARGIN = 20
    }

    private val generator = DungeonGenerator(DungeonGenerator.Settings(0.6f, 4, 10, 1f))
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

    private fun calculateScale(): ZoomInfo {
        val minLeft = generator.rooms.asSequence().map { it.room.topLeft.x }.min() ?: 0f
        val maxRight = generator.rooms.asSequence().map { it.room.topLeft.x + it.room.width }.max() ?: 0f
        val minTop = generator.rooms.asSequence().map { it.room.topLeft.y }.min() ?: 0f
        val maxBottom = generator.rooms.asSequence().map { it.room.topLeft.y + it.room.height }.max() ?: 0f

        val maxWidth = maxRight - minLeft
        val maxHeight = maxBottom - minTop

        val horizontalScale = (width - 2 * SCREEN_MARGIN) / maxWidth
        val verticalScale = (height - 2 * SCREEN_MARGIN) / maxHeight
        val scale = listOf(verticalScale, horizontalScale, SCALE_FACTOR.toFloat()).min()!!

        val leftPos = (minLeft - scaledWidth / 2) * scale + width / 2f
        val rightPos = (maxRight - scaledWidth / 2) * scale + width / 2f
        val topPos = (minTop - scaledHeight / 2) * scale + height / 2f
        val bottomPos = (maxBottom - scaledHeight / 2) * scale + height / 2f

        val translateX = when {
            leftPos < SCREEN_MARGIN -> SCREEN_MARGIN - leftPos
            rightPos > width - SCREEN_MARGIN -> (width - SCREEN_MARGIN) - rightPos
            else -> 0f
        }
        val translateY = when {
            topPos < SCREEN_MARGIN -> SCREEN_MARGIN - topPos
            bottomPos > height - SCREEN_MARGIN -> (height - SCREEN_MARGIN) - bottomPos
            else -> 0f
        }

        return ZoomInfo(scale, translateX, translateY)
    }

    private fun DungeonGenerator.Room.getRect(zoomInfo: ZoomInfo): RectF {
        val left = (topLeft.x - scaledWidth / 2f) * zoomInfo.scaleFactor + this@DungeonGeneratorScene.width / 2f + zoomInfo.translateX
        val top = (topLeft.y - scaledHeight / 2f) * zoomInfo.scaleFactor + this@DungeonGeneratorScene.height / 2f + zoomInfo.translateY
        val right = left + width * zoomInfo.scaleFactor
        val bottom = top + height * zoomInfo.scaleFactor

        return RectF(left, top, right, bottom)
    }

    private data class ZoomInfo(val scaleFactor: Float, val translateX: Float, val translateY: Float)
}