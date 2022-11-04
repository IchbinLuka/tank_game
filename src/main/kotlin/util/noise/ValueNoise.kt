package util.noise

import core.HeightMap
import util.getLinearGradient
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class ValueNoise(
    private val width: Int,
    private val depth: Int,
    private val octaves: Int = 10,
    private val startFrequencyX: Int = 4,
    private val startFrequencyY: Int = 4,
    private val startMaxHeight: Int = 64
) : BoundedNoise() {
    private var heightData: HeightMap = Array(width) { DoubleArray(depth) { 0.0 } }

    private val random = Random.Default

    /** Calculates a 2D Value Noise using the given parameters. */
    override fun calculate(): HeightMap {
        var xFrequency = startFrequencyX
        var yFrequency = startFrequencyY
        var maxHeight = startMaxHeight.toDouble()
        for (i in 0 until octaves) {
            val tempData = Array(width) { DoubleArray(depth) { 0.0 } }
            val deltaX: Int = max(width / (xFrequency), 1)
            val deltaY: Int = max(depth / (yFrequency), 1)
            // Generate random points
            for (k in 0 until xFrequency) {
                for (m in 0 until yFrequency) {
                    tempData[k * deltaX][m * deltaY] = (random.nextDouble() - 0.5).pow(2) * 4 * maxHeight
                }
            }
            // Log.v(TAG, "DeltaX: $deltaX")
            // Interpolation
            for (k in 0 until width) {
                val xDistance = (k % deltaX)
                val previousX = k - xDistance
                val nextX = min(k + deltaX - xDistance, width - 1)
                for (m in 0 until depth) {
                    val yDistance = (m % deltaY)
                    val previousY = m - yDistance
                    val nextY = min(m + deltaY - yDistance, depth - 1)
                    val first = getLinearGradient(
                        start = tempData[previousX][previousY],
                        end = tempData[nextX][previousY],
                        progress = xDistance / deltaX.toDouble()
                    )
                    val second = getLinearGradient(
                        start = tempData[previousX][nextY],
                        end = tempData[nextX][nextY],
                        progress = xDistance / deltaX.toDouble()
                    )
                    tempData[k][m] = getLinearGradient(
                        start = first,
                        end = second,
                        progress = yDistance / deltaY.toDouble()
                    )
                }
            }
            // Add values to heightData
            tempData.forEachIndexed { k, arr ->
                arr.forEachIndexed { m, value ->
                    heightData[k][m] += value
                }
            }
            maxHeight /= 2
            // double the frequencies
            xFrequency = min(xFrequency shl 1, width) // min to avoid deltaX, deltaY being 0
            yFrequency = min(yFrequency shl 1, depth)
        }
        return heightData
    }
}