package io.github.pshegger.gamedevexperiments.scenes.balls

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.BallsMenuScene
import io.github.pshegger.gamedevexperiments.utils.Ball
import io.github.pshegger.gamedevexperiments.geometry.Vector
import io.github.pshegger.gamedevexperiments.geometry.Velocity
import java.util.*

/**
 * @author pshegger@gmail.com
 */
class SimpleBallsScene(val gameSurfaceView: GameSurfaceView) : Scene {
    var balls = mutableListOf<Ball>()
    var width: Int = 0
    var height: Int = 0

    var btnAdd: Button? = null

    val statPaint = Paint().apply {
        textSize = 42f
        isAntiAlias = true
        color = Color.GRAY
    }

    override fun sizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height

        val rng = Random()
        balls = (1..100).map { generateBall(rng) }.toMutableList()

        btnAdd = Button("ADD", width - 200f, height - 120f).apply {
            setOnClickListener { balls.add(generateBall(rng)) }
        }
    }

    override fun update(deltaTime: Long) {
        balls.forEach {
            it.move(deltaTime)
            it.checkWallCollision(width, height)
        }

        btnAdd?.update(deltaTime, gameSurfaceView.touch)
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))

        balls.forEach { it.render(canvas) }

        canvas.drawText("Count: ${balls.size}", 10f, height - 10f, statPaint)

        btnAdd?.render(canvas)
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = BallsMenuScene(gameSurfaceView)
    }

    private fun generateBall(rng: Random): Ball {
        val c = Vector(rng.nextFloat() * width, rng.nextFloat() * height)
        val dir = Vector(rng.nextFloat() * 2 - 1, rng.nextFloat() * 2 - 1).normalize()
        val v = Velocity(rng.nextFloat() * 500 + 500, dir)
        val color = Color.rgb(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255))
        val r = rng.nextFloat() * 15 + 15

        return Ball(c, r, color, v)
    }
}
