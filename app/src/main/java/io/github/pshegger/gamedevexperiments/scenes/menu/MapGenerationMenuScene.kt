package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.scenes.EmptyScene
import io.github.pshegger.gamedevexperiments.scenes.GraphBuildingScene
import io.github.pshegger.gamedevexperiments.scenes.VoronoiScene

/**
 * @author gergely.hegedus@tappointment.com
 */
class MapGenerationMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Graph Building", GraphBuildingScene(gameSurfaceView)),
                MenuItem("Perlin Noise", EmptyScene(gameSurfaceView)),
                MenuItem("Voronoi", VoronoiScene(gameSurfaceView)),
                MenuItem("Map Generation", EmptyScene(gameSurfaceView))
        )

    override val title: String
        get() = "Map Generation"

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }
}