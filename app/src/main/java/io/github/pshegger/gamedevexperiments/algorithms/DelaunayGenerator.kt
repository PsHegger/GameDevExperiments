package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Triangle
import io.github.pshegger.gamedevexperiments.geometry.Vector
import io.github.pshegger.gamedevexperiments.utils.*

/**
 * @author pshegger@gmail.com
 */
class DelaunayGenerator(val points: List<Vector>) {
    val edges: List<Edge>
        get() = triangles.flatMap { it.edges }
    val canGenerateMore: Boolean
        get() = unprocessedPoints.isNotEmpty() || containsHelperTriangle()

    private val unprocessedPoints = arrayListOf<Vector>().apply { addAll(points) }

    private val triangles = arrayListOf<Triangle>()

    private var width: Int = 0
    private var height: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height
        val m = 2.5f * Math.max(width, height)

        triangles.clear()
        triangles.add(Triangle(
                Vector(-10f, -10f),
                Vector(-10f, m),
                Vector(m, -10f)
        ))

        unprocessedPoints.clear()
        unprocessedPoints.addAll(points)
    }

    fun generateNextEdge() {
        val pair = nonDelaunayPair()
        if (pair != null) {
            makeFlip(pair)
            return
        }

        if (unprocessedPoints.isEmpty()) {
            val finalTriangles = triangles.filterNot { it.isHelper(width, height) }
            triangles.clear()
            triangles.addAll(finalTriangles)
            return
        }

        val p = unprocessedPoints.random()
        unprocessedPoints.remove(p)
        val container = triangles.first { it.contains(p) }

        triangles.remove(container)
        triangles.add(Triangle(container.a, container.b, p))
        triangles.add(Triangle(container.a, container.c, p))
        triangles.add(Triangle(container.b, container.c, p))
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNextEdge()
        }
    }

    private fun makeFlip(p: Pair<Triangle, Triangle>) {
        val t1 = p.first
        val t2 = p.second
        triangles.remove(t1)
        triangles.remove(t2)

        val commonEdge = t1.commonEdge(t2)
        if (commonEdge != null) {
            val o1 = t1.thirdPoint(commonEdge.start, commonEdge.end)
            val o2 = t2.thirdPoint(commonEdge.start, commonEdge.end)
            if (o1 != null && o2 != null) {
                triangles.add(Triangle(o1, commonEdge.start, o2))
                triangles.add(Triangle(o2, commonEdge.end, o1))
            }
        }
    }

    private fun nonDelaunayPair(): Pair<Triangle, Triangle>? = triangles.flatMap { t1 ->
        triangles.others(t1).filter { t2 -> t1.commonEdge(t2) != null }.map { Pair(t1, it) }
    }.find { p ->
        val commonEdge = p.first.commonEdge(p.second)!!
        val otherPoint = p.second.thirdPoint(commonEdge.start, commonEdge.end)!!
        p.first.circumscribedCircle.contains(otherPoint)
    }

    private fun containsHelperTriangle() = triangles.any { it.isHelper(width, height) }
    private fun Triangle.isHelper(width: Int, height: Int) = isPointHelper(a, width, height) || isPointHelper(b, width, height) || isPointHelper(c, width, height)
    private fun isPointHelper(v: Vector, width: Int, height: Int) = v.x < 0 || v.x > width || v.y < 0 || v.y > height
}