package io.github.pshegger.gamedevexperiments.algorithms

/**
 * @author pshegger@gmail.com
 */
class MapGenerator {
    val canGenerateMore: Boolean
        get() = false

    fun generateNext() {
        // todo
    }

    fun generateAll() {
        while (canGenerateMore) {
            generateNext()
        }
    }
}