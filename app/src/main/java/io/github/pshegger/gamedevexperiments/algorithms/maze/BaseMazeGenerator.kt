package io.github.pshegger.gamedevexperiments.algorithms.maze

import io.github.pshegger.gamedevexperiments.utils.times

/**
 * @author gergely.hegedus@tappointment.com
 */
abstract class BaseMazeGenerator {
    enum class FieldValue {
        Empty, Wall, Active, NotProcessed
    }

    protected var width: Int = 0
    protected var height: Int = 0

    val fields: ArrayList<ArrayList<FieldValue>> = arrayListOf()
    val finished: Boolean
        get() = fields.all { row -> row.all { it != FieldValue.NotProcessed } }

    abstract fun nextStep()

    fun generateAll() {
        while (!finished) {
            nextStep()
        }
    }

    open fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height

        clearFields()
    }

    protected fun clearFields() {
        fields.clear()
        (1..height).forEach {
            val row = arrayListOf<FieldValue>()
            (1..width).forEach {
                row.add(FieldValue.NotProcessed)
            }
            fields.add(row)
        }
    }

    protected fun getField(c: Coordinate) = fields[c.y][c.x]
    protected fun setField(c: Coordinate, value: FieldValue) {
        fields[c.y][c.x] = value
    }

    protected fun buildTunnel(start: Coordinate, end: Coordinate) {
        val dirX = when {
            end.x < start.x -> -1
            start.x < end.x -> 1
            else -> 0
        }

        val dirY = when {
            end.y < start.y -> -1
            start.y < end.y -> 1
            else -> 0
        }

        var x = start.x
        var y = start.y
        while (getField(end) == FieldValue.NotProcessed) {
            fields[y][x] = FieldValue.Empty
            x += dirX
            y += dirY
        }
    }

    protected data class Coordinate(val x: Int, val y: Int) {
        val neighbors: List<Coordinate>
            get() = let { listOf(-1, 0, 1) }
                    .let { it * it }
                    .filter { Math.abs(it[0]) != Math.abs(it[1]) }
                    .map { Coordinate(x + it[0], y + it[1]) }

        fun valid(w: Int, h: Int) = x >= 0 && y >= 0 && x < w && y < h
    }
}