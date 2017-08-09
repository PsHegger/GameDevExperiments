package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.algorithms.maze.RandomDepthFirstGenerator
import io.github.pshegger.gamedevexperiments.algorithms.maze.RandomTraversalGenerator
import io.github.pshegger.gamedevexperiments.scenes.MazeScene
import io.github.pshegger.gamedevexperiments.scenes.PathFindingScene

/**
 * @author pshegger@gmail.com
 */
class MazeMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }

    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Random Traversal", MazeScene(gameSurfaceView, RandomTraversalGenerator())),
                MenuItem("Random Depth-First", MazeScene(gameSurfaceView, RandomDepthFirstGenerator())),
                MenuItem("Path-Finding", PathFindingScene(gameSurfaceView))
        )
    override val title: String
        get() = "Maze"
}