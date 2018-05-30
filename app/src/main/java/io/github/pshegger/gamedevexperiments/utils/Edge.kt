package io.github.pshegger.gamedevexperiments.utils

/**
 * @author pshegger@gmail.com
 */
data class Edge(val start: Vector, val end: Vector) {
    val middle: Vector by lazy { start + ((end - start) / 2f) }
    val direction: Vector by lazy { Vector(end.x - start.x, end.y - start.y) }
    val normal: Vector by lazy { Vector(-direction.y, direction.x) }
}