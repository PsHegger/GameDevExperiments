package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.scenes.poisson.PoissonBestCandidateScene
import io.github.pshegger.gamedevexperiments.scenes.poisson.PoissonBridsonScene

/**
 * @author pshegger@gmail.com
 */
class PoissonMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val title: String
        get() = "Poisson"

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }

    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Best-Candidate", PoissonBestCandidateScene(gameSurfaceView)),
                MenuItem("Bridson", PoissonBridsonScene(gameSurfaceView))
        )
}