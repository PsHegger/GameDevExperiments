package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.hud.Button
import io.github.pshegger.gamedevexperiments.scenes.menu.BallsMenuScene
import io.github.pshegger.gamedevexperiments.utils.Ball
import io.github.pshegger.gamedevexperiments.utils.Vector
import io.github.pshegger.gamedevexperiments.utils.Velocity
import java.util.*

/**
 * @author gergely.hegedus@tappointment.com
 */
class BouncingBallsScene(val gameSurfaceView: GameSurfaceView) : Scene {
    var balls = listOf<Ball>()
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
        balls = (1..50).map { generateBall(rng) }

        btnAdd = Button("ADD", width - 200f, height - 120f, width - 40f, height - 40f, Color.TRANSPARENT, Color.GRAY, Color.BLACK, 50f).apply {
            onClick = { balls += listOf(generateBall(rng)) }
        }
    }

    override fun update(deltaTime: Long) {
        balls.forEach { ball ->
            ball.move(deltaTime)
            ball.checkWallCollision(width, height)

            val collide = balls.firstOrNull { ob -> ball.collidesWithBall(ob) }

            collide?.let {
                val d = (ball.c - collide.c).length() - (ball.r + collide.r)
                ball.c = ball.c + ball.v.dir * d
                val n = (collide.c - ball.c).normalize()
                ball.v = ball.v.reflect(n)
            }
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