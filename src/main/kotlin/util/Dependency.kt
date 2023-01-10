package util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asDeferred
import three.GLTF

abstract class Dependency<T> {
    val value: T? get() = _value
    private var _value: T? = null

    abstract val assetName: String

    suspend fun initValue() {
        _value = prepareValueAsync().await()
    }

    protected abstract fun prepareValueAsync(): Deferred<T>

    override fun hashCode(): Int {
        return assetName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Dependency<*> && other.assetName == assetName
    }
}

class GLTFDependency(
    override val assetName: String
) : Dependency<GLTF>() {
    override fun prepareValueAsync(): Deferred<GLTF> = ResourceManager.gltfLoader.loadAsync(assetName).asDeferred()
}

interface AssetInitializer {
    abstract suspend fun initAssets()
}