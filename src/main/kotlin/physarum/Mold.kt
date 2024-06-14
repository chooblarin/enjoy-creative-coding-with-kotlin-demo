package physarum

import org.openrndr.draw.ColorBufferShadow
import org.openrndr.extra.noise.Random
import org.openrndr.math.Vector2
import kotlin.math.cos
import kotlin.math.sin

class Mold(
    var position: Vector2,
    private var heading: Double
) {
    private class Sensor {
        var rightPosition = Vector2.ZERO
        var leftPosition = Vector2.ZERO
        var forwardPosition = Vector2.ZERO

        fun updatePosition(base: Vector2, heading: Double) {
            val (x, y) = base

            val areaWidth = Simulation.Settings.AREA_WIDTH
            val areaHeight = Simulation.Settings.AREA_HEIGHT
            val angle = Simulation.Parameters.sensorAngle
            val distance = Simulation.Parameters.sensorDistance

            // right
            val rpx = x + distance * cos(heading + angle)
            val rpy = y + distance * sin(heading + angle)
            rightPosition = Vector2(
                (rpx + areaWidth) % areaWidth,
                (rpy + areaHeight) % areaHeight
            )

            // left
            val lpx = x + distance * cos(heading - angle)
            val lpy = y + distance * sin(heading - angle)
            leftPosition = Vector2(
                (lpx + areaWidth) % areaWidth,
                (lpy + areaHeight) % areaHeight
            )

            // forward
            val fpx = x + distance * cos(heading)
            val fpy = y + distance * sin(heading)
            forwardPosition = Vector2(
                (fpx + areaWidth) % areaWidth,
                (fpy + areaHeight) % areaHeight
            )
        }
    }

    private val sensor = Sensor()

    fun update(field: ColorBufferShadow) {
        // Sense and rotate
        sensor.updatePosition(position, heading)

        val rv = field[sensor.rightPosition.x.toInt(), sensor.rightPosition.y.toInt()].luminance
        val lv = field[sensor.leftPosition.x.toInt(), sensor.leftPosition.y.toInt()].luminance
        val fv = field[sensor.forwardPosition.x.toInt(), sensor.forwardPosition.y.toInt()].luminance

        val rotAngle = Simulation.Parameters.moldRotationAngle
        when {
            fv > lv && fv > rv -> {
                // no change
            }
            fv < lv && fv < rv -> {
                val sig = if (Random.bool(0.5)) 1.0 else -1.0
                heading += sig * rotAngle
            }
            lv > rv -> heading -= rotAngle
            rv > lv -> heading += rotAngle
        }

        // Move
        val step = Simulation.Parameters.moldStepSize
        val dp = Vector2(cos(heading), sin(heading)) * step
        var x = position.x + dp.x
        var y = position.y + dp.y

        // torus boundary
        val areaWidth = Simulation.Settings.AREA_WIDTH
        val areaHeight = Simulation.Settings.AREA_HEIGHT

        x = (x + areaWidth) % areaWidth
        y = (y + areaHeight) % areaHeight

        position = Vector2(x, y)
    }
}
