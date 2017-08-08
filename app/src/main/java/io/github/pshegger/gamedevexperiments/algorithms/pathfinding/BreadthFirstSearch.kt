package io.github.pshegger.gamedevexperiments.algorithms.pathfinding

import io.github.pshegger.gamedevexperiments.algorithms.maze.BaseMazeGenerator
import io.github.pshegger.gamedevexperiments.utils.random
import java.util.*

/**
 * @author gergely.hegedus@tappointment.com
 */
class BreadthFirstSearch(maze: List<List<BaseMazeGenerator.FieldValue>>) : BasePathFinder(maze) {
    private val path: Stack<Coordinate> = Stack()
    private val visitedCells: MutableSet<Coordinate> = mutableSetOf()

    private var pathFound: Boolean = false

    override fun nextStep() {
        if (path.empty()) {
            path.push(start)
            visitedCells.add(start)
        }

        if (!pathFound) {
            val c = path.peek()
            val dsts = c.possibleDestinations - visitedCells

            if (dsts.isNotEmpty()) {
                val dst = dsts.random()

                if (dst == stop) {
                    pathFound = true
                } else {
                    path.push(dst)
                    _states.add(FieldState(dst.x, dst.y, FieldState.FieldValue.Active))
                }

            } else {
                path.pop()
                removeState(c)
            }

            visitedCells.add(c)
        } else {
            val c = path.pop()
            removeState(c)
            _states.add(FieldState(c.x, c.y, FieldState.FieldValue.Path))
        }
    }
}