package io.github.pshegger.gamedevexperiments.scenes

import android.graphics.Canvas
import android.graphics.Color
import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.Scene
import io.github.pshegger.gamedevexperiments.scenes.menu.MainMenuScene

/**
 * @author pshegger@gmail.com
 */
class EmptyScene(val gameSurfaceView: GameSurfaceView) : Scene {
    override fun sizeChanged(width: Int, height: Int) {
    }

    override fun update(deltaTime: Long) {
    }

    override fun render(canvas: Canvas) {
        canvas.drawColor(Color.rgb(154, 206, 235))
    }

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }
}