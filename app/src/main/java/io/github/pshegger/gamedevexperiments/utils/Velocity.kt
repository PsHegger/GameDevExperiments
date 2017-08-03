package io.github.pshegger.gamedevexperiments.utils

/**
 * @author gergely.hegedus@tappointment.com
 */
data class Velocity(val speed: Float, val dir: Vector) {
    val x: Float
        get() = speed * dir.x

    val y: Float
        get() = speed * dir.y

    fun oppositeX() = copy(dir = dir.copy(x = -dir.x))
    fun oppositeY() = copy(dir = dir.copy(y = -dir.y))
    fun reflect(n: Vector) = copy(dir = dir.reflect(n))
}