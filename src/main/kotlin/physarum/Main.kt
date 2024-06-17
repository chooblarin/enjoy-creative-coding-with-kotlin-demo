package physarum

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.invert
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extra.fx.blur.BoxBlur
import org.openrndr.extra.fx.color.ColorCorrection
import org.openrndr.extra.gui.WindowedGUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.Random
import org.openrndr.math.Vector2
import kotlin.math.PI

fun main() = application {
    configure {
        width = Simulation.Settings.AREA_WIDTH.toInt()
        height = Simulation.Settings.AREA_HEIGHT.toInt()
        windowAlwaysOnTop = true
    }
    program {
        val gui = WindowedGUI()
        gui.add(Simulation.Parameters, "Parameters")

        val trailTarget = renderTarget(width, height) { colorBuffer() }

        val diffuse = BoxBlur()
        val decay = ColorCorrection()

        diffuse.addTo(gui, "Blur")
        decay.addTo(gui, "Color Correction").apply {
            opacity = 0.9
        }

        val molds = List(Simulation.Settings.MOLD_COUNT) { createRandomMold() }

        extend(gui)

        extend {
            backgroundColor = if (Simulation.Parameters.invertColor) ColorRGBa.WHITE else ColorRGBa.BLACK

            // Diffuse and decay
            diffuse.apply(trailTarget.colorBuffer(0), trailTarget.colorBuffer(0))
            decay.apply(trailTarget.colorBuffer(0), trailTarget.colorBuffer(0))

            val s = trailTarget.colorBuffer(0).shadow.apply { download() }

            drawer.isolatedWithTarget(trailTarget) {
                stroke = null
                fill = ColorRGBa.WHITE.opacify(Simulation.Parameters.moldColorOpacity)

                when (Simulation.Parameters.option) {
                    MoldDisplayMode.Circle -> {
                        circles {
                            for (m in molds) {
                                circle(m.position, Simulation.Parameters.moldBodySize)
                                m.update(s)
                            }
                        }
                    }
                    MoldDisplayMode.Point -> {
                        points {
                            for (m in molds) {
                                point(m.position)
                                m.update(s)
                            }
                        }
                    }
                }
            }

            if (Simulation.Parameters.invertColor) {
                drawer.drawStyle.colorMatrix = invert
            }

            drawer.image(trailTarget.colorBuffer(0))
        }
    }
}

fun createRandomMold(): Mold {
    val x = Random.double0(Simulation.Settings.AREA_WIDTH)
    val y = Random.double0(Simulation.Settings.AREA_HEIGHT)
    val heading = Random.double0(2.0 * PI)
    return Mold(
        Vector2(x, y),
        heading
    )
}
