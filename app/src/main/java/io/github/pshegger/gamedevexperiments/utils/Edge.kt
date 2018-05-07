package io.github.pshegger.gamedevexperiments.utils

/**
 * @author pshegger@gmail.com
 */
data class Edge(val start: Vector, val end: Vector) {
    val middle: Vector
        get() = start + ((end - start) / 2f)
}