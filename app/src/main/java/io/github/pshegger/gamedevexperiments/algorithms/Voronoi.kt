package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Polygon
import io.github.pshegger.gamedevexperiments.geometry.Triangle
import io.github.pshegger.gamedevexperiments.geometry.Vector
import io.github.pshegger.gamedevexperiments.utils.others
import io.github.pshegger.gamedevexperiments.utils.random

/**
 * @author pshegger@gmail.com
 */
class Voronoi(private val triangles: List<Triangle>) {
    val canGenerateMore: Boolean
        get() = processingQueue.isNotEmpty()
    val polygons: List<Polygon>
        get() = _polygons
    val points: List<PointState>
        get() = _points.map { PointState(it, processingQueue.contains(it)) }

    private val processedPoints = mutableListOf<Vector>()
    private val processingQueue = mutableListOf<Vector>()
    private val _polygons = mutableListOf<Polygon>()
    private val _points = triangles.flatMap { listOf(it.a, it.b, it.c) }.distinct()

    fun reset() {
        processedPoints.clear()
        processingQueue.clear()
        processingQueue.add(_points.random())
        _polygons.clear()
    }

    fun generateNextEdge() {
        val p = processingQueue[0]
        val polygon = _polygons.find { it.p == p } ?: let {
            val poly = Polygon(p)
            _polygons.add(poly)
            poly
        }
        val pTriangles = triangles.filter { t -> t.a == p || t.b == p || t.c == p }
        val neighborPairs = pTriangles.flatMap { t1 -> pTriangles.others(t1).filter { t2 -> t1.commonEdge(t2) != null }.map { t2 -> Pair(t1, t2) } }
        val unconnectedPair = neighborPairs.firstOrNull { p -> polygon.edges.none { e -> e == Edge(p.first.circumscribedCircleCenter, p.second.circumscribedCircleCenter) } }

        if (unconnectedPair == null) {
            processingQueue.remove(p)
            processedPoints.add(p)
            return
        }

        val edge = Edge(unconnectedPair.first.circumscribedCircleCenter, unconnectedPair.second.circumscribedCircleCenter)
        polygon.edges.add(edge)
        (unconnectedPair.first.points + unconnectedPair.second.points).distinct().forEach { point ->
            if (!processingQueue.contains(point) && !processedPoints.contains(point)) {
                processingQueue.add(point)
            }
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNextEdge()
        }
    }

    data class PointState(val p: Vector, val isActive: Boolean)
}