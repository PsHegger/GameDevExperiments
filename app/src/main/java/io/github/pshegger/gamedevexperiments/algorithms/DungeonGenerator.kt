package io.github.pshegger.gamedevexperiments.algorithms

import android.graphics.RectF
import io.github.pshegger.gamedevexperiments.geometry.Vector
import java.util.Random

/**
 * @author pshegger@gmail.com
 */
class DungeonGenerator(private val settings: Settings) {
    private val _rooms = arrayListOf<RoomState>()
    val rooms: List<RoomState>
        get() = _rooms
    val canGenerateMore: Boolean
        get() = !roomsGenerated || !roomsPlaced

    private val roomsGenerated: Boolean
        get() = _rooms.sumBy { it.room.area() } >= (width * height) * settings.fillRatio
    private val roomsPlaced: Boolean
        get() = rooms.all { it.state == RoomState.State.Placed }

    private val rng = Random(settings.seed ?: System.currentTimeMillis())

    private var width: Int = 0
    private var height: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height
        rng.setSeed(settings.seed ?: System.currentTimeMillis())
        _rooms.clear()
    }

    fun nextStep() {
        if (!roomsGenerated) {
            val width = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
            val height = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
            val left = (this.width - width) / 2f
            val top = (this.height - height) / 2f
            _rooms.add(RoomState(Room(Vector(left, top), width, height), RoomState.State.Generated))
            return
        }

        if (_rooms.none { it.state == RoomState.State.Placed }) {
            _rooms[rng.nextInt(_rooms.size)].state = RoomState.State.Placed
        }

        val movingRoom = _rooms.firstOrNull { it.state == RoomState.State.Selected }

        if (movingRoom != null) {
            val moveVector = Vector(Math.cos(movingRoom.direction).toFloat(), Math.sin(movingRoom.direction).toFloat())
            movingRoom.room.topLeft = movingRoom.room.topLeft + moveVector
            val finalPlace = _rooms
                    .filter { it.state === RoomState.State.Placed }
                    .none { it.room.intersects(movingRoom.room, settings.roomMargin) }
            if (finalPlace) {
                movingRoom.state = RoomState.State.Placed
            }
        } else {
            val remainingRooms = _rooms.filter { it.state == RoomState.State.Generated }
            remainingRooms[rng.nextInt(remainingRooms.size)].apply {
                state = RoomState.State.Selected
                direction = rng.nextDouble() * 2 * Math.PI
            }
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            nextStep()
        }
    }

    data class Room(var topLeft: Vector, val width: Int, val height: Int) {
        val center: Vector
            get() = Vector(topLeft.x + width / 2f, topLeft.y + height / 2f)

        fun area() = width * height
        fun intersects(other: Room, margin: Float) = getRect(margin).intersect(other.getRect(0f))
        private fun getRect(margin: Float) = RectF(topLeft.x - margin, topLeft.y - margin, topLeft.x + width + margin, topLeft.y + height + margin)
    }

    data class RoomState(val room: Room, var state: State, var direction: Double = 0.0) {
        enum class State {
            Generated, Placed, Selected
        }
    }

    data class Settings(val fillRatio: Float, val minSize: Int, val maxSize: Int, val roomMargin: Float = 0f, val seed: Long? = null)
}