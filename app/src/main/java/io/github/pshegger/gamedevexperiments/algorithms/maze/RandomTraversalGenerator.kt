package io.github.pshegger.gamedevexperiments.algorithms.maze

import io.github.pshegger.gamedevexperiments.utils.random
import kotlin.collections.ArrayList

/**
 * @author gergely.hegedus@tappointment.com
 */
class RandomTraversalGenerator : BaseMazeGenerator() {
    private var started: Boolean = false
    private val possibleWays: ArrayList<Coordinate> = arrayListOf()

    override fun nextStep() {
        if (!started) {
            started = true

            val start = Coordinate(0, 0)

            setField(start, FieldValue.Active)
            possibleWays.add(start)

            return
        }

        var foundAWay = false

        while (!foundAWay) {
            if (possibleWays.isEmpty()) {
                foundAWay = true
                continue
            }

            val c = possibleWays.random()

            val dests = c.possibleDestinations

            if (dests.isNotEmpty()) {
                val dst = dests.random()
                buildTunnel(c, dst)

                possibleWays.add(dst)
                setField(dst, FieldValue.Active)

                foundAWay = true
            } else {
                possibleWays.remove(c)
                setField(c, FieldValue.Empty)
            }
        }

        if (possibleWays.isEmpty()) {
            fields.indices.firstOrNull { y -> fields[y].any { it == FieldValue.NotProcessed } }
                    ?.let { y ->
                        fields[y].indices.filter { fields[y][it] == FieldValue.NotProcessed }
                                .forEach { x ->
                                    fields[y][x] = FieldValue.Wall
                                }
                    }
        }
    }

    override fun reset(width: Int, height: Int) {
        super.reset(width, height)

        started = false
        possibleWays.clear()
    }

    private val Coordinate.possibleDestinations: List<Coordinate>
        get() = listOf(
                Coordinate(x - 2, y),
                Coordinate(x + 2, y),
                Coordinate(x, y - 2),
                Coordinate(x, y + 2)
        ).filter {
            it.valid(width, height) && getField(it) == FieldValue.NotProcessed
        }
}