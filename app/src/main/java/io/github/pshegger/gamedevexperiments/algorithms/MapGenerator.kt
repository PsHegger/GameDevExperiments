package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Vector

/**
 * @author pshegger@gmail.com
 */
class MapGenerator {
    val canGenerateMore: Boolean
        get() = poissonGenerator.canGenerateMore || delaunayGenerator.canGenerateMore || voronoi.canGenerateMore

    val poissonPoints: List<PoissonBridson.PointState>
        get() = poissonGenerator.points

    val delaunayEdges: List<Edge>
        get() = delaunayGenerator.edges
    val delaunayPoints: List<Vector>
        get() = delaunayGenerator.points

    val voronoiEdges: List<Edge>
        get() = voronoi.edges
    val voronoiPoints: List<Voronoi.PointState>
        get() = voronoi.points

    val state: State
        get() = _state

    private var poissonGenerator = PoissonBridson()
    private var delaunayGenerator = DelaunayGenerator(emptyList())
    private var voronoi = Voronoi(emptyList())
    private var _state: State = State.Poisson

    private var width: Int = 0
    private var height: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height

        poissonGenerator = PoissonBridson(margin = 5, radius = 80)
        poissonGenerator.reset(this.width, this.height)
        _state = State.Poisson
    }

    fun generateNext() {
        if (poissonGenerator.canGenerateMore) {
            poissonGenerator.generateNextPoint()
            if (!poissonGenerator.canGenerateMore) {
                delaunayGenerator = DelaunayGenerator(poissonGenerator.points.map { it.p })
                delaunayGenerator.reset(width, height)
                _state = State.Delaunay
            }

            return
        }

        if (delaunayGenerator.canGenerateMore) {
            delaunayGenerator.generateNextEdge()
            if (!delaunayGenerator.canGenerateMore) {
                voronoi = Voronoi(delaunayGenerator.triangles)
                voronoi.reset()
                _state = State.Voronoi
            }
            return
        }

        if (voronoi.canGenerateMore) {
            voronoi.generateNextEdge()
            if (!voronoi.canGenerateMore) {
                _state = State.Finished
            }
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNext()
        }
    }

    enum class State {
        Poisson, Delaunay, Voronoi, Finished
    }
}