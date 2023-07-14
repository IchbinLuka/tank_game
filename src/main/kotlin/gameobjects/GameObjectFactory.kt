package gameobjects

import core.gameobjects.GameObject
import core.gameobjects.SynchronizedInitializer
import kotlinx.coroutines.sync.Mutex

abstract class GameObjectFactory<T, K>
    where T : GameObject, T: SynchronizedInitializer {
    private var initialized = false

    private val mutex = Mutex()

    suspend fun prepareAssets() {
        if (initialized) return
        mutex.lock()
        loadAssets()
        initialized = true
        mutex.unlock()
    }

    protected abstract suspend fun loadAssets()

    protected abstract fun createGameObject(arg: K): T

    fun newGameObject(arg: K): T {
        if (!initialized) throw IllegalStateException("Factory is not initialized")
        return createGameObject(arg).apply {
            initChildren()
            init()
        }
    }
}