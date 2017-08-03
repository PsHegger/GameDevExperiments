package io.github.pshegger.gamedevexperiments.utils

/**
 * @author gergely.hegedus@tappointment.com
 */
fun List<Edge>.neighbors(v: Vector): List<Vector> = this.map {
    if (it.start == v) {
        it.end
    } else if (it.end == v) {
        it.start
    } else {
        null
    }
}.filterNotNull()

fun <T> List<T>.random(): T = this[Math.floor(Math.random() * this.size).toInt()]
operator fun <T> List<T>.times(o: List<T>) = this.flatMap { a -> o.map { b -> kotlin.collections.listOf(a, b) } }