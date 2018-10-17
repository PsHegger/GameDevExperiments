package io.github.pshegger.gamedevexperiments.algorithms

import android.graphics.RectF
import android.util.Log
import io.github.pshegger.gamedevexperiments.geometry.Edge
import io.github.pshegger.gamedevexperiments.geometry.Vector
import java.util.Random

/**
 * @author pshegger@gmail.com
 */
class DungeonGenerator(private val settings: Settings) {
    private val _rooms = arrayListOf<RoomState>()
    private val corridors = arrayListOf<Graph.Edge<Room>>()

    val rooms: List<RoomState>
        get() = _rooms
    val edges: List<Edge>
        get() = corridors.map { Edge(it.start.center, it.end.center) }
    val canGenerateMore: Boolean
        get() = generationStep != GenerationStep.Finished

    private var generationStep: GenerationStep = GenerationStep.RoomGeneration

    private val rng = Random(settings.seed ?: System.currentTimeMillis())

    private var width: Int = 0
    private var height: Int = 0
    private var delayCtr: Int = 0

    fun reset(width: Int, height: Int) {
        this.width = width
        this.height = height
        rng.setSeed(settings.seed ?: System.currentTimeMillis())
        _rooms.clear()
        corridors.clear()
        generationStep = GenerationStep.RoomGeneration
    }

    fun nextStep() {
        when (generationStep) {
            GenerationStep.RoomGeneration -> generateRoom()
            GenerationStep.RoomMovement -> moveRoom()
            GenerationStep.RoomSelection -> selectRoom()
            GenerationStep.SpanningTreeGeneration -> generateSpanningTree()
            else -> Log.d("DungeonGenerator", "The generation is already over")
        }
    }

    fun generateAll() {
        while (canGenerateMore) {
            nextStep()
        }
    }

    private fun generateRoom() {
        val width = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
        val height = rng.nextInt(settings.maxSize - settings.minSize + 1) + settings.minSize
        val left = (this.width - width) / 2f
        val top = (this.height - height) / 2f
        _rooms.add(RoomState(Room(Vector(left, top), width, height), RoomState.State.Generated))

        if (_rooms.asSequence().map { it.room.area() }.sum() >= this.width * this.height * settings.fillRatio) {
            generationStep = GenerationStep.RoomMovement
        }
    }

    private fun moveRoom() {
        if (_rooms.none { it.state == RoomState.State.Placed }) {
            _rooms[rng.nextInt(_rooms.size)].state = RoomState.State.Placed
        }

        val movingRoom = _rooms.firstOrNull { it.state == RoomState.State.Moving }

        if (movingRoom != null) {
            val moveVector = Vector(Math.cos(movingRoom.direction).toFloat(), Math.sin(movingRoom.direction).toFloat())
            var ctr = 0
            var finalPlace = false
            while (ctr < 3 && !finalPlace) {
                movingRoom.room.topLeft = movingRoom.room.topLeft + moveVector
                finalPlace = _rooms
                        .filter { it.state === RoomState.State.Placed }
                        .none { it.room.intersects(movingRoom.room, settings.roomMargin) }
                ctr++
            }
            if (finalPlace) {
                movingRoom.state = RoomState.State.Placed
                if (_rooms.all { it.state == RoomState.State.Placed }) {
                    delayCtr = 0
                    generationStep = GenerationStep.RoomSelection
                }
            }
        } else {
            val remainingRooms = _rooms.filter { it.state == RoomState.State.Generated }
            remainingRooms[rng.nextInt(remainingRooms.size)].apply {
                state = RoomState.State.Moving
                direction = rng.nextDouble() * 2 * Math.PI
            }
        }
    }

    private fun selectRoom() {
        delayCtr++
        if (delayCtr < 6) {
            return
        }
        val minArea = _rooms.map { it.room.area() }.average() * settings.finalAreaRatio
        val possibleRooms = _rooms.filter { it.state == RoomState.State.Placed && it.room.area() >= minArea }
        possibleRooms.take(1).forEach { it.state = RoomState.State.Selected }
        if (possibleRooms.size == 1) {
            generationStep = GenerationStep.SpanningTreeGeneration
        }
        delayCtr = 0
    }

    private fun generateSpanningTree() {
        delayCtr++
        if (delayCtr < 6) {
            return
        }
        val finalRooms = _rooms.filter { it.state == RoomState.State.Selected }
        val graph = Graph(finalRooms.map { it.room }, corridors)

        finalRooms.mapIndexed { i, x ->
            finalRooms.mapIndexed { j, y ->
                if (i < j) Pair(x.room, y.room) else null
            }.filterNotNull()
        }.flatten()
                .filterNot { graph.isRouteAvailable(it.first, it.second) }
                .sortedBy { it.first.center.distance(it.second.center) }
                .take(1)
                .forEach { corridors.add(Graph.Edge(it.first, it.second)) }

        if (corridors.size == finalRooms.size - 1) {
            generationStep = GenerationStep.Finished
        }
        delayCtr = 0
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
            Generated, Placed, Moving, Selected
        }
    }

    data class Settings(val fillRatio: Float, val minSize: Int, val maxSize: Int, val roomMargin: Float = 0f, val finalAreaRatio: Float, val seed: Long? = null)

    private enum class GenerationStep {
        RoomGeneration, RoomMovement, RoomSelection, SpanningTreeGeneration, Finished
    }
}