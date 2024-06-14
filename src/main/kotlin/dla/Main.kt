package dla

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.noise.uniform
import org.openrndr.math.IntVector2
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import kotlin.math.floor

fun main() = application {
    configure {
        width = 640
        height = 640
    }
    program {
        var agents = drawer.bounds.scatter(2.0)
            .map { Agent.createFromPoint(it) }
        val radius = 3.0

        val resultsTarget = renderTarget(width, height) {
            colorBuffer()
        }
        val movingParticlesTarget = renderTarget(width, height) {
            colorBuffer()
        }

        /* Draw seed */
        drawer.isolatedWithTarget(resultsTarget) {
            clear(ColorRGBa.TRANSPARENT)
            stroke = null
            fill = ColorRGBa.BLACK.opacify(0.2)
            circle(drawer.bounds.center, 5.0)
        }

        val field = Circle(drawer.bounds.center, drawer.bounds.width / 2.0)

        backgroundColor = ColorRGBa.WHITE

        extend {
            drawer.isolatedWithTarget(movingParticlesTarget) {
                clear(ColorRGBa.TRANSPARENT)
                circles {
                    agents.forEach {
                        if (!it.hit) {
                            stroke = null
                            fill = ColorRGBa.BLUE
                            circle(it.position, radius)
                        }
                    }
                }
            }

            val shadow = resultsTarget.colorBuffer(0).shadow
            shadow.download()

            val nextAgents = mutableListOf<Agent>()

            agents.forEach { agent ->
                agent.update()
                agent.constrain(drawer.bounds)

                if (field.contains(agent.position)) {
                    val (sx, sy) = agent.intPosition()
                    val color = shadow[sx, sy]
                    if (color.alpha > 0.1) {
                        drawer.isolatedWithTarget(resultsTarget) {
                            stroke = null
                            fill = ColorRGBa.BLACK.opacify(0.2)
                            circle(agent.position, radius)
                        }
                        val spawned = Agent.spawn(drawer.bounds)
                        nextAgents.add(spawned)
                    } else {
                        nextAgents.add(agent)
                    }
                }
            }

            agents = nextAgents

            drawer.image(resultsTarget.colorBuffer(0))
            drawer.image(movingParticlesTarget.colorBuffer(0))
        }
    }
}

class Agent(var position: Vector2) {
    companion object {
        fun spawn(bounds: Rectangle): Agent {
            val theta = Double.uniform(0.0, Math.PI * 2)
            val l = Polar(theta, bounds.width / 2.0).cartesian
            val p = bounds.center + l
            return Agent(p)
        }

        fun createFromPoint(point: Vector2): Agent {
            return Agent(point)
        }
    }

    var hit = false

    fun update() {
        if (hit) {
            return
        }
        val velocity = Vector2.uniform(-1.0, 1.0).normalized * 4.0
        position += velocity
    }

    fun constrain(bounds: Rectangle) {
        var nextX = position.x
        var nextY = position.y

        if (nextX < 0.0) {
            nextX += bounds.width
        }
        if (nextX >= bounds.width) {
            nextX %= bounds.width
        }
        if (nextY < 0.0) {
            nextY += bounds.height
        }
        if (nextY >= bounds.height) {
            nextY %= bounds.height
        }
        position = Vector2(nextX, nextY)
    }

    fun intPosition(): IntVector2 {
        val sx = floor(position.x).toInt()
        val sy = floor(position.y).toInt()
        return IntVector2(sx, sy)
    }
}
