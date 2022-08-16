package util.noise

import kotlin.math.abs
import kotlin.math.pow

class GroundNoise(
    private val width: Int = 200,
    private val depth: Int = 20,
) : BoundedNoise() {
    override fun calculate(): HeightMap {
        val data = ValueNoise(
            width = width, height = depth
        ).allValues()
        val invertNoise = data.mapIndexed { i, doubles ->
            doubles.mapIndexed { j, d ->
                d - abs(data[i][j] - d) * 0.05 * j.toDouble().pow(2)
            }.toDoubleArray()
        }.toTypedArray()
        return invertNoise
    }
}