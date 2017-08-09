package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.MainActivity
import io.github.pshegger.gamedevexperiments.scenes.*

/**
 * @author pshegger@gmail.com
 */
class MainMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val title: String
        get() = "Main Menu"

    override fun onBackPressed() {
        MainActivity.instance.finish()
    }

    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Balls", BallsMenuScene(gameSurfaceView)),
                MenuItem("Poisson", PoissonMenuScene(gameSurfaceView)),
                MenuItem("Voronoi", VoronoiScene(gameSurfaceView)),
                MenuItem("Maze", MazeMenuScene(gameSurfaceView))
        )
}