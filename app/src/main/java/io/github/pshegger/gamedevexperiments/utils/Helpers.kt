package io.github.pshegger.gamedevexperiments.utils

import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Vector

/**
 * @author pshegger@gmail.com
 */
fun List<Edge>.neighbors(v: Vector): List<Vector> = this.mapNotNull {
    when (v) {
        it.start -> it.end
        it.end -> it.start
        else -> null
    }
}

fun <T> List<T>.random(): T = this[Math.floor(Math.random() * this.size).toInt()]
operator fun <T> List<T>.times(o: List<T>) = this.flatMap { a -> o.map { b -> kotlin.collections.listOf(a, b) } }