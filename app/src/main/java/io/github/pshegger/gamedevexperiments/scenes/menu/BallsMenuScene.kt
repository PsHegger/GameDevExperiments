package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.scenes.BouncingBallsScene
import io.github.pshegger.gamedevexperiments.scenes.SimpleBallsScene

/**
 * @author pshegger@gmail.com
 */
class BallsMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val title: String
        get() = "Balls"

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }

    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Simple", SimpleBallsScene(gameSurfaceView)),
                MenuItem("Bouncing", BouncingBallsScene(gameSurfaceView))
        )
}