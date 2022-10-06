package tests

import core.HeightMap
import util.noise.BoundedNoise

class FlatNoise(
    private val width: Int,
    private val depth: Int
): BoundedNoise() {
    override fun calculate(): HeightMap {
        return Array<DoubleArray>(width) {
            DoubleArray(depth) { 0.0 }
        }
    }
}