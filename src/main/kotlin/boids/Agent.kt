package boids

import org.openrndr.math.Vector2

interface Agent {
    var position: Vector2
    var velocity: Vector2
    var forces: MutableList<Vector2>

    fun interact(neighbors: List<Agent>)
    fun move(dt: Double) {
        position += velocity * dt
    }
}
