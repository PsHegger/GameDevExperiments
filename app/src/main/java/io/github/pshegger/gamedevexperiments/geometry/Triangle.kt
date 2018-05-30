package io.github.pshegger.gamedevexperiments.geometry

/**
 * @author pshegger@gmail.com
 */
data class Triangle(val a: Vector, val b: Vector, val c: Vector) {
    val edges: List<Edge> = listOf(
            Edge(a, b),
            Edge(a, c),
            Edge(b, c)
    )

    val circumscribedCircle: Circle by lazy {
        val e1Middle = edges[0].middle
        val e1Dir = edges[0].direction
        val a1 = e1Dir.x
        val b1 = e1Dir.y
        val c1 = a1 * e1Middle.x + b1 * e1Middle.y

        val e2Middle = edges[1].middle
        val e2Dir = edges[1].direction
        val a2 = e2Dir.x
        val b2 = e2Dir.y
        val c2 = a2 * e2Middle.x + b2 * e2Middle.y

        val cx = ((c1 * b2) - (c2 * b1)) / (a1 * b2 - a2 * b1)
        val cy = (c1 - a1 * cx) / b1
        val center = Vector(cx, cy)
        Circle(center, center.distance(a))
    }

    fun contains(p: Vector): Boolean {
        val d = ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y))
        val aa = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / d
        val bb = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / d
        val cc = 1 - aa - bb

        return aa in 0.0..1.0 && bb in 0.0..1.0 && cc in 0.0..1.0
    }

    fun commonEdge(o: Triangle): Edge? = edges.find { e1 -> o.edges.any { e2 -> e1 == e2 } }
    fun thirdPoint(p1: Vector, p2: Vector): Vector? = when (p1) {
        a -> if (p2 == b) c else if (p2 == c) b else null
        b -> if (p2 == a) c else if (p2 == c) a else null
        c -> if (p2 == a) b else if (p2 == b) a else null
        else -> null
    }
}