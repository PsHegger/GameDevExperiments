package io.github.pshegger.gamedevexperiments.algorithms

import io.github.pshegger.gamedevexperiments.geometry.Vector
import java.util.Random

/**
 * @author pshegger@gmail.com
 */
class DungeonGenerator(private val settings: Settings) {
    private val _rooms = arrayListOf<Room>()
    val rooms: List<Room>
        get() = _rooms
    val canGenerateMore: Boolean
        get() = !roomsGenerated

    private val roomsGenerated: Boolean
        get() = _rooms.sumBy { it.area() } >= (width * height) * settings.fillRatio

    private val rng = Random(settings.seed ?: System.currentTimeMillis())

    private var width: Int = 0
    private var height: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height
        _rooms.clear()
    }

    fun nextStep() {
        if (!roomsGenerated) {
            val width = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
            val height = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
            val left = (this.width - width) / 2f
            val top = (this.height - height) / 2f
            _rooms.add(Room(Vector(left, top), width, height))
            return
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            nextStep()
        }
    }

    data class Room(var topLeft: Vector, val width: Int, val height: Int) {
        fun area() = width * height
    }

    data class Settings(val fillRatio: Float, val minSize: Int, val maxSize: Int, val seed: Long? = null)
}