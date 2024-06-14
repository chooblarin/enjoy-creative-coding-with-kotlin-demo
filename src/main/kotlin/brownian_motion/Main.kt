package brownian_motion

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.WindowedGUI
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.math.Vector2

fun main() = application {
    configure {
        width = 640
        height = 640
    }
    program {
        val params = object {
            @DoubleParameter("Step size", 0.01, 5.0)
            var step = 1.0
        }

        val gui = WindowedGUI()
        gui.add(params, "Parameters")

        var points = drawer.bounds.scatter(5.0)

        extend(gui)
        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.stroke = null
            drawer.fill = ColorRGBa.BLACK.opacify(0.2)
            drawer.circles(points, 3.0)

            val next = points.map {
                val rnd = Vector2.uniform(-1.0, 1.0)
                val velocity = rnd.normalized * params.step
                it + velocity
            }
            points = next
        }
    }
}
