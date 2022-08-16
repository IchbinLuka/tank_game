package util.noise

interface Noise {
    fun valueAt(x: Double, y: Double): Double?
}

abstract class BoundedNoise: Noise {
    private var cachedData: HeightMap? = null
    protected abstract fun calculate(): HeightMap

    fun allValues(): HeightMap = cachedData ?: calculate().apply { cachedData = this@apply }

    override fun valueAt(x: Double, y: Double): Double? {
        val data = allValues()
        return data.getOrNull(x.toInt())?.getOrNull(y.toInt())
    }
}