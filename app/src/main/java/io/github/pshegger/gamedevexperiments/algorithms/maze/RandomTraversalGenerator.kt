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

        while (!foundAWay && !possibleWays.isEmpty()) {
            val c = possibleWays.random()

            val destinations = c.possibleDestinations

            if (destinations.isNotEmpty()) {
                val dst = destinations.random()
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
}