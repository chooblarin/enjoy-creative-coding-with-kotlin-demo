package boids

import org.openrndr.math.Vector2
import org.openrndr.math.average
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class Boid(
    override var position: Vector2,
    override var velocity: Vector2,
    override var forces: MutableList<Vector2> = mutableListOf()
) : Agent {
    override fun interact(neighbors: List<Agent>) {
        forces.clear()

        val areaWidth = Simulation.Settings.AREA_WIDTH
        val areaHeight = Simulation.Settings.AREA_HEIGHT
        val wallAvoidance = avoidWall(areaWidth, areaHeight)
        forces.add(wallAvoidance)

        val separation = separation(neighbors) * Simulation.Parameters.separationFactor
        val alignment = alignment(neighbors) * Simulation.Parameters.alignmentFactor
        val cohesion = cohesion(neighbors) * Simulation.Parameters.cohesionFactor

        forces.add(separation)
        forces.add(alignment)
        forces.add(cohesion)

        var newVelocity = velocity.copy()
        for (f in forces) {
            newVelocity += f
        }

        // limit turn rate
        newVelocity = limitAngleChange(velocity, newVelocity)

        // speed limit
        val maxSpeed = Simulation.Settings.BOID_SPEED_MAX
        val minSpeed = Simulation.Settings.BOID_SPEED_MIN
        val squaredLen = newVelocity.squaredLength
        newVelocity = when {
            squaredLen == 0.0 -> Vector2.ZERO
            squaredLen > maxSpeed * maxSpeed -> newVelocity.normalized * maxSpeed
            squaredLen < minSpeed * minSpeed -> newVelocity.normalized * minSpeed
            else -> newVelocity
        }

        velocity = newVelocity
    }

    private fun avoidWall(areaWidth: Double, areaHeight: Double): Vector2 {
        var force = Vector2.ZERO
        val dmin = 10e-6

        val leftDist = max(position.x, dmin)
        force += Vector2(1.0, 0.0) * (1.0 / (leftDist * leftDist))

        val rightDist = max(areaWidth - position.x, dmin)
        force += Vector2(-1.0, 0.0) * (1.0 / (rightDist * rightDist))

        val bottomDist = max(position.y, dmin)
        force += Vector2(0.0, 1.0) * (1.0 / (bottomDist * bottomDist))

        val topDist = max(areaHeight - position.y, dmin)
        force += Vector2(0.0, -1.0) * (1.0 / (topDist * topDist))

        return force * Simulation.Settings.WALL_AVOIDANCE_FACTOR
    }

    private fun separation(neighbors: List<Agent>): Vector2 = neighbors.fold(Vector2.ZERO) { acc, n ->
        val distance = position - n.position
        acc + distance.normalized * (1.0 / distance.squaredLength)
    }

    private fun alignment(neighbors: List<Agent>): Vector2 = neighbors.fold(Vector2.ZERO) { acc, n ->
        val theta = atan2(n.velocity.y, n.velocity.x)
        val unit = Vector2(cos(theta), sin(theta))
        acc + unit
    }

    private fun cohesion(neighbors: List<Agent>): Vector2 {
        if (neighbors.isEmpty()) {
            return Vector2.ZERO
        }
        return neighbors.map { it.position }.average() - position
    }

    private fun limitAngleChange(from: Vector2, to: Vector2): Vector2 {
        val toAngle = Math.toDegrees(atan2(to.y, to.x))
        val fromAngle = Math.toDegrees(atan2(from.y, from.x))
        // https://math.stackexchange.com/questions/1649841/signed-angle-difference-without-conditions/1649850#1649850
        val delta = (toAngle - fromAngle + 540) % 360 - 180
        val maxTurnRate = Simulation.Parameters.boidMaxTurnRate
        return when {
            delta > maxTurnRate -> from.rotate(maxTurnRate)
            delta < -maxTurnRate -> from.rotate(-maxTurnRate)
            else -> from.rotate(delta)
        }
    }
}
