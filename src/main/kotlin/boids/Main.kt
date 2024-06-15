package boids

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.WindowedGUI
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.quadtree.Quadtree
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.rotateZ
import org.openrndr.math.transforms.scale
import org.openrndr.math.transforms.translate
import org.openrndr.shape.Rectangle
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
    configure {
        width = Simulation.Settings.AREA_WIDTH.toInt()
        height = Simulation.Settings.AREA_HEIGHT.toInt()
    }
    program {
        val gui = WindowedGUI()
        gui.add(Simulation.Parameters, "Parameters")

        val boids = List(Simulation.Settings.BOIDS_AMOUNT) { createBoidSomewhere(Simulation.Settings.AREA_BOUNDS) }
        var quadtree = Quadtree<Boid>(Simulation.Settings.AREA_BOUNDS) { it.position }
        var lastTime = 0.0

        backgroundColor = ColorRGBa.WHITE

        extend(gui)
        extend {
            val deltaTime = seconds - lastTime
            val nextTree = Quadtree<Boid>(Simulation.Settings.AREA_BOUNDS) { it.position }

            drawer.fill = ColorRGBa.BLACK
            drawer.stroke = null

            val cs = boids.map { boid ->
                val radius = Simulation.Parameters.boidPerceptionRadius
                val neighbors = quadtree.nearest(boid, radius)?.neighbours ?: emptyList()
                boid.interact(neighbors)
                boid.move(deltaTime)
                nextTree.insert(boid)
                boidContour(boid)
            }

            drawer.contours(cs)

            quadtree = nextTree
            lastTime = seconds
        }
    }
}

/* Generate a random boid within the field */
fun createBoidSomewhere(rect: Rectangle): Boid {
    val pos = Vector2.uniform(rect)
    val theta = Random.double0(2.0 * PI)
    val vel = Vector2(cos(theta), sin(theta)) * 80.0
    return Boid(pos, vel)
}

val boidShape = contour {
    moveTo(1.2, 0.0)
    lineTo(-0.4, 0.3)
    lineTo(-0.4, -0.3)
    close()
}

fun boidContour(boid: Boid): ShapeContour {
    val mt = Matrix44.translate(boid.position.x, boid.position.y, 0.0)
    val mr = Matrix44.rotateZ(Math.toDegrees(atan2(boid.velocity.y, boid.velocity.x)))
    val s = Simulation.Parameters.boidBodyScale
    val ms = Matrix44.scale(s, s, 0.0)
    return boidShape.transform(mt * mr * ms)
}
