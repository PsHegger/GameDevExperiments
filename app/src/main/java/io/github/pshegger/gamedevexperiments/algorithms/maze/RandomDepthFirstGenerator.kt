package io.github.pshegger.gamedevexperiments.algorithms.maze

import io.github.pshegger.gamedevexperiments.utils.random
import java.util.*

/**
 * @author gergely.hegedus@tappointment.com
 */
class RandomDepthFirstGenerator : BaseMazeGenerator() {
    private var started = false
    private val possibleWays: Stack<Coordinate> = Stack()

    override fun nextStep() {
        if (!started) {
            started = true

            val start = Coordinate(0, 0)

            setField(start, FieldValue.Active)
            possibleWays.push(start)

            return
        }

        if (possibleWays.isEmpty()) {
            fields.indices.firstOrNull { y -> fields[y].any { it == FieldValue.NotProcessed } }
                    ?.let { y ->
                        fields[y].indices.filter { fields[y][it] == FieldValue.NotProcessed }
                                .forEach { x ->
                                    fields[y][x] = FieldValue.Wall
                                }
                    }
        } else {
            val last = possibleWays.peek()
            val destinations = last.possibleDestinations

            if (destinations.isNotEmpty()) {
                val dst = destinations.random()
                buildTunnel(last, dst)

                possibleWays.push(dst)
                setField(last, FieldValue.Active)
                setField(dst, FieldValue.Active)
            } else {
                setField(possibleWays.pop(), FieldValue.Empty)
            }
        }
    }

    override fun reset(width: Int, height: Int) {
        super.reset(width, height)

        started = false
        possibleWays.clear()
    }
}