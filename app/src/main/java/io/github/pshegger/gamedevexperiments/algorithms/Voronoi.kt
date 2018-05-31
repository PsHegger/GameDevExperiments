package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.geometry.Triangle

/**
 * @author pshegger@gmail.com
 */
class Voronoi(val triangles: List<Triangle>) {
    val canGenerateMore: Boolean
        get() = false

    fun generateNextEdge() {
        // todo
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNextEdge()
        }
    }
}