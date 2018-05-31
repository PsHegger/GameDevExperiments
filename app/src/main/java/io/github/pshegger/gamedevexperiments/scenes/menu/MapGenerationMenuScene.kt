package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.scenes.EmptyScene
import io.github.pshegger.gamedevexperiments.scenes.DelaunayBuildingScene
import io.github.pshegger.gamedevexperiments.scenes.MapGeneratorScene
import io.github.pshegger.gamedevexperiments.scenes.VoronoiScene

/**
 * @author pshegger@gmail.com
 */
class MapGenerationMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Delaunay Building", DelaunayBuildingScene(gameSurfaceView)),
                MenuItem("Perlin Noise", EmptyScene(gameSurfaceView)),
                MenuItem("Voronoi", VoronoiScene(gameSurfaceView)),
                MenuItem("Map Generation", MapGeneratorScene(gameSurfaceView))
        )

    override val title: String
        get() = "Map Generation"

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }
}