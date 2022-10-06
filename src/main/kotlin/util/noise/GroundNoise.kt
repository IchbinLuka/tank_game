package util.noise

import core.HeightMap
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
        for (i in data.indices) {
            for (j in data[i].indices) {
                val value = data[i][j]
                val pref = data[i].getOrElse(j - 1) { value }
                data[i][j] = pref - (abs(pref - value)) * j.toDouble().pow(2) * 0.01
            }
        }
        return data
    }
}