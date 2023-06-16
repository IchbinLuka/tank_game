package noise

import HeightMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

class GroundNoise(
    private val width: Int = 200,
    private val depth: Int = 20,
    private val min: Double = 0.0
) : BoundedNoise() {
    override fun calculate(): HeightMap {
        val data = ValueNoise(
            width = width, depth = depth
        ).allValues()
        for (i in data.indices) {
            for (j in 0 until data[i].lastIndex) {
                val value = data[i][j]
                val pref = data[i].getOrElse(j - 1) { value }
                data[i][j] = pref - (abs(pref - value)) * j.toDouble().pow(2) * 0.005
                data[i][j] = max(min, data[i][j])
            }
            data[i][data[i].lastIndex] = min
        }
        return data
    }
}