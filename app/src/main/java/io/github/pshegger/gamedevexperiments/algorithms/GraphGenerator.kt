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
        get() = unprocessedPoints.isNotEmpty()

    private val unprocessedPoints = arrayListOf<Vector>().apply { addAll(points) }

    private val triangles = arrayListOf<Triangle>()

    init {
        triangles.add(Triangle(
                Vector(-1000000f, -1000000f),
                Vector(-1000000f, 1000000f),
                Vector(1000000f, -1000000f)
        ))
    }

    fun generateNextEdge() {
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

    data class Triangle(val a: Vector, val b: Vector, val c: Vector) {
        fun edges(): List<Edge> = listOf(
                Edge(a, b),
                Edge(a, c),
                Edge(b, c)
        )

        fun contains(p: Vector): Boolean {
            return true
        }
    }
}