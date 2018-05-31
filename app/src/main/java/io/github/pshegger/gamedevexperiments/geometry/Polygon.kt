package io.github.pshegger.gamedevexperiments.geometry

/**
 * @author gergely.hegedus@tappointment.com
 */
data class Polygon(val p: Vector, val edges: MutableList<Edge> = mutableListOf())