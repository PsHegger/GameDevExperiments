package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.utils.Vector
import java.util.*

/**
 * @author gergely.hegedus@tappointment.com
 */
class PoissonBestCandidate(val margin: Int = 0, val candidateCount: Int = 10) {
    val points = arrayListOf<Vector>()
    val rng = Random()

    var maxPointCount: Int = 0
    var width: Int = 0
    var height: Int = 0

    fun reset(width: Int, height: Int, maxPointCount: Int = 0) {
        points.clear()
        this.width = width
        this.height = height
        this.maxPointCount = maxPointCount
    }

    fun generateNextPoint() {
        if (points.isEmpty()) {
            points.add(randomPoint())
            return
        }

        var bestCandidate: Vector? = null
        var bestDistance: Float = 0f

        (1..candidateCount).forEach {
            val c = randomPoint()
            val d = c.distance(findClosest(c))
            if (d > bestDistance) {
                bestCandidate = c
                bestDistance = d
            }
        }

        bestCandidate?.let {
            points.add(it)
        }
    }

    fun generateAll() {
        while (points.size < maxPointCount) {
            generateNextPoint()
        }
    }

    private fun randomPoint() = Vector(rng.nextFloat() * (width - 2 * margin) + margin, rng.nextFloat() * (height - 2 * margin) + margin)

    private fun findClosest(c: Vector) = points.asSequence().sortedBy { it.distance(c) }.first()
}