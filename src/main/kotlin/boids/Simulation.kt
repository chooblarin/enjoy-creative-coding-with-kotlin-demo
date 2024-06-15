package boids

import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.shape.Rectangle

object Simulation {
    object Settings {
        const val BOIDS_AMOUNT = 800
        const val AREA_WIDTH = 960.0
        const val AREA_HEIGHT = 640.0
        val AREA_BOUNDS = Rectangle(0.0, 0.0, AREA_WIDTH, AREA_HEIGHT)

        const val BOID_SPEED_MAX = 100.0
        const val BOID_SPEED_MIN = 40.0
        const val WALL_AVOIDANCE_FACTOR = 5e3
    }
    object Parameters {
        @DoubleParameter("separation", 0.0, 2500.0, order = 0)
        var separationFactor = 250.0

        @DoubleParameter("alignment", 0.0, 5.0, order = 1)
        var alignmentFactor = 0.75

        @DoubleParameter("cohesion", 0.0, 0.5, order = 2)
        var cohesionFactor = 0.05

        @DoubleParameter("boid scale", 0.5, 10.0)
        var boidBodyScale = 4.0

        @DoubleParameter("boid max turn rate", 1.0, 90.0)
        var boidMaxTurnRate = 20.0

        @DoubleParameter("boid perception radius", 1.0, 200.0)
        var boidPerceptionRadius = 50.0
    }
}
