package io.github.pshegger.gamedevexperiments.geometry

/**
 * @author pshegger@gmail.com
 */
data class Circle(val center: Vector, val radius: Float) {
    fun contains(p: Vector) = center.distance(p) < radius
}