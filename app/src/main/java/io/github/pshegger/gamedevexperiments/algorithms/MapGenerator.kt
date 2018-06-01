package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.algorithms.simplex.SimplexNoise
import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Polygon
import io.github.pshegger.gamedevexperiments.geometry.Vector
import java.util.*

/**
 * @author pshegger@gmail.com
 */
class MapGenerator {
    val canGenerateMore: Boolean
        get() = poissonGenerator.canGenerateMore || delaunayGenerator.canGenerateMore || voronoi.canGenerateMore || _mapPolygons.size < sortedCells.size

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

    val mapPolygons: List<MapPolygon>
        get() = _mapPolygons

    val state: State
        get() = _state

    private var poissonGenerator = PoissonBridson()
    private var delaunayGenerator = DelaunayGenerator(emptyList())
    private var voronoi = Voronoi(emptyList())
    private var _state: State = State.Poisson

    private var sortedCells: List<Polygon> = emptyList()
    private var mapPolygonValues: List<Double> = emptyList()
    private val _mapPolygons = mutableListOf<MapPolygon>()

    private var width: Int = 0
    private var height: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height

        poissonGenerator = PoissonBridson(margin = 5, radius = 40)
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
                sortedCells = voronoi.polygons.sortedBy { it.p.y * width + it.p.x }
                val simplex = SimplexNoise(100, 0.1, Random().nextInt())
                _mapPolygons.clear()

                val simplexValues = sortedCells.map { c -> simplex.getNoise(c.p.x.toInt(), c.p.y.toInt()) }
                val minSimplex = simplexValues.min()!!
                val maxSimplex = simplexValues.max()!!
                mapPolygonValues = simplexValues.map { (it - minSimplex) / (maxSimplex - minSimplex) }

                _state = State.Simplex
            }
            return
        }

        if (_mapPolygons.size < sortedCells.size) {
            val i = _mapPolygons.size
            val mapPoly = MapPolygon(sortedCells[i], Math.round(255 * mapPolygonValues[i]).toInt())
            _mapPolygons.add(mapPoly)
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNext()
        }
    }

    enum class State {
        Poisson, Delaunay, Voronoi, Simplex, Finished
    }

    data class MapPolygon(val polygon: Polygon, val value: Int)
}