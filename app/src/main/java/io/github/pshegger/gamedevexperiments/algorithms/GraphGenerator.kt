package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.utils.Edge
import io.github.pshegger.gamedevexperiments.utils.Vector
import io.github.pshegger.gamedevexperiments.utils.random

/**
 * @author pshegger@gmail.com
 */
class GraphGenerator(val points: List<Vector>) {
    val edges: List<Edge>
        get() = triangles.flatMap { it.edges() }
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

    private fun containsHelperTriangle() = triangles.any { it.isHelper(width, height) }

    data class Triangle(val a: Vector, val b: Vector, val c: Vector) {
        fun edges(): List<Edge> = listOf(
                Edge(a, b),
                Edge(a, c),
                Edge(b, c)
        )

        fun contains(p: Vector): Boolean {
            val d = ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y))
            val aa = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / d
            val bb = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / d
            val cc = 1 - aa - bb

            return aa in 0.0..1.0 && bb in 0.0..1.0 && cc in 0.0..1.0
        }

        fun isHelper(width: Int, height: Int) = isPointHelper(a, width, height) || isPointHelper(b, width, height) || isPointHelper(c, width, height)

        private fun isPointHelper(v: Vector, width: Int, height: Int) = v.x < 0 || v.x > width || v.y < 0 || v.y > height
    }
}