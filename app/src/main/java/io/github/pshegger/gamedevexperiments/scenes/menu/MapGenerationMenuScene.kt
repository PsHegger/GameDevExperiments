package io.github.pshegger.gamedevexperiments.scenes.menu

import io.github.pshegger.gamedevexperiments.GameSurfaceView
import io.github.pshegger.gamedevexperiments.scenes.map.DelaunayBuildingScene
import io.github.pshegger.gamedevexperiments.scenes.map.MapGeneratorScene
import io.github.pshegger.gamedevexperiments.scenes.map.SimplexGeneratorScene
import io.github.pshegger.gamedevexperiments.scenes.map.VoronoiScene

/**
 * @author pshegger@gmail.com
 */
class MapGenerationMenuScene(gameSurfaceView: GameSurfaceView) : BaseMenuScene(gameSurfaceView) {
    override val scenes: List<MenuItem>
        get() = listOf(
                MenuItem("Delaunay Building", DelaunayBuildingScene(gameSurfaceView)),
                MenuItem("Simplex Noise", SimplexGeneratorScene(gameSurfaceView)),
                MenuItem("Voronoi", VoronoiScene(gameSurfaceView)),
                MenuItem("Map Generation", MapGeneratorScene(gameSurfaceView))
        )

    override val title: String
        get() = "Map Generation"

    override fun onBackPressed() {
        gameSurfaceView.scene = MainMenuScene(gameSurfaceView)
    }
}