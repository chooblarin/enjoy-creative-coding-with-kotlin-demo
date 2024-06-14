package physarum

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extra.fx.blur.BoxBlur
import org.openrndr.extra.fx.color.ColorCorrection
import org.openrndr.extra.gui.WindowedGUI
import org.openrndr.extra.gui.addTo

fun main() = application {
    configure {
        width = 640
        height = 640
    }
    program {
        val gui = WindowedGUI()

        val dataTarget = renderTarget(width, height) { colorBuffer() }

        val diffuse = BoxBlur()
        val decay = ColorCorrection()

        diffuse.addTo(gui, "Blur")
        decay.addTo(gui, "Color Correction").apply {
            opacity = 0.9
        }

        extend(gui)

        extend {
            // Diffuse and decay
            dataTarget.apply { diffuse.apply(colorBuffer(0), colorBuffer(0)) }
            dataTarget.apply { decay.apply(colorBuffer(0), colorBuffer(0)) }

            drawer.isolatedWithTarget(dataTarget) {
                val p = mouse.position
                fill = ColorRGBa.WHITE
                stroke = null
                circle(p, 10.0)
            }

            drawer.image(dataTarget.colorBuffer(0))
        }
    }
}