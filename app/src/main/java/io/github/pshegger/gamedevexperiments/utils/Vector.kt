package io.github.pshegger.gamedevexperiments.utils

/**
 * @author gergely.hegedus@tappointment.com
 */
data class Vector(val x: Float, val y: Float) {
    fun normalize() = this / length()

    fun length() = Math.sqrt(x * x + y * y.toDouble()).toFloat()

    fun reflect(n: Vector) = this - n * 2f * (this * n)

    fun distance(o: Vector) = (o - this).length()

    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
    operator fun minus(v: Vector): Vector = Vector(x - v.x, y - v.y)
    operator fun unaryMinus(): Vector = Vector(-x, -y)

    operator fun div(d: Float): Vector = Vector(x / d, y / d)
    operator fun times(r: Float): Vector = Vector(x * r, y * r)
    operator fun times(v: Vector): Float = x * v.x + y * v.y            // Dot product
}