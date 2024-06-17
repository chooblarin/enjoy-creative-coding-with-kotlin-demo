package physarum

import org.openrndr.extra.parameters.BooleanParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.OptionParameter
import kotlin.math.PI

object Simulation {
    object Settings {
        const val AREA_WIDTH = 640.0
        const val AREA_HEIGHT = 640.0
        const val MOLD_COUNT = 30_000
    }

    object Parameters {
        @DoubleParameter("Sensor angle", 0.0, PI)
        var sensorAngle = PI / 4.0

        @DoubleParameter("Sensor distance", 0.0, 100.0)
        var sensorDistance = 10.0

        @DoubleParameter("Mold Step Size", 0.1, 10.0)
        var moldStepSize = 1.0

        @DoubleParameter("Mold Rotation Angle", 0.0, PI)
        var moldRotationAngle = PI / 4.0

        @DoubleParameter("Mold Body Size", 0.1, 10.0)
        var moldBodySize = 1.0

        @DoubleParameter("Mold Color Opacity", 0.0, 1.0)
        var moldColorOpacity = 1.0

        @OptionParameter("Mold Display Mode")
        var option = MoldDisplayMode.Circle

        @BooleanParameter("Invert Color")
        var invertColor = true
    }
}

enum class MoldDisplayMode {
    Circle,
    Point,
}
