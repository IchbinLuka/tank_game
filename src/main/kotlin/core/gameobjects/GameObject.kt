package core.gameobjects

import GameMessage
import core.ConnectionManager
import core.currentScene
import ext.TAG
import ext.getWorldEuler
import ext.set
import gameobjects.GameObjectFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import three.js.Euler
import three.js.Object3D
import three.js.PositionalAudio
import three.js.Vector3
import util.Log
import util.Uuid
import kotlin.collections.set
import kotlin.math.max
import kotlin.reflect.KClass

abstract class GameObject : Nameable {
    abstract val renderable: Object3D?

    /** A unique Identifier for the GameObject */
    val uuid = Uuid.next()

    override val name: String = uuid.toString()

    protected open val initialChildren: Collection<GameObject> = listOf()

    /** [MutableMap] containing the relevant GameObjects */
    protected val _children: MutableMap<String, GameObject> = hashMapOf()
    val children: Map<String, GameObject> by ::_children
    // open val _children: MutableSet<GameObject> = hashSetOf()

    open var health: Double = 1.0
        protected set

    open val invincible: Boolean = true

    open var position: Vector3
        get() = renderable?.position ?: Vector3()
        set(value) {
            renderable?.position?.set(value)
        }

    open val globalPosition: Vector3
        get() = renderable?.getWorldPosition(Vector3()) ?: Vector3()

    open val globalRotation: Euler
        get() = renderable?.getWorldEuler(Euler()) ?: Euler()

    /** Indicates whether the object should longer be a part of the scene */
    var alive: Boolean = true
        protected set

    open fun registerMultiplayerSynchronized(manager: ConnectionManager) {
        for (child in children.values) {
            child.registerMultiplayerSynchronized(manager)
        }
    }

    fun createAudio(): PositionalAudio = PositionalAudio(currentScene.audioListener)

    fun addGameObject(gameObject: GameObject) {
        _children[gameObject.name] = gameObject
        MainScope().launch {
            gameObject.preloadAssets()
            gameObject.renderable?.let { renderable?.add(it) }
            gameObject.onSceneReady()
        }
    }

    fun <A: GameObject, B, T: GameObjectFactory<A, B>> addSynchronized(factory: T, args: B): A {
        val o = factory.newGameObject(args)
        _children[o.name] = o
        o.renderable?.let { renderable?.add(it) }
        return o
    }

    open fun damage(amount: Double) {
        if (invincible || health == 0.0) return
        health = max(health - amount, 0.0)
        if (health == 0.0) onDeath()
    }

    fun die() {
        alive = false
        onDeath()
    }

    protected open fun onDeath() {}

    fun findObjectWithNameAndType(name: String? = null, type: KClass<*>): Any? {
        if (name == null) {
            for (gameObject in children.values) {
                if (type.isInstance(gameObject)) return gameObject
            }
        } else {
            val gameObject = children[name]
            if (type.isInstance(gameObject)) return gameObject
        }
        return null
    }

    inline fun <reified T> findObject(name: String? = null): T? = findObjectWithNameAndType(name, T::class) as T?

    inline fun <reified T> findObjects(): Collection<T> = children.values.filterIsInstance<T>()


    override fun equals(other: Any?): Boolean = other is GameObject && other.uuid == uuid
    override fun hashCode(): Int = uuid.toInt()

    fun initChildren() {
        for (child in _children.values) {
            child.initChildren()
        }
        for (child in initialChildren) {
            _children[child.name] = child
        }
    }

    /** Used for loading things that are crucial to the scene. The scene will be shown after this completed */
    open suspend fun preloadAssets() {
        initChildren()
        _children.values.forEach { it.preloadAssets() }
        _children.values.forEach { o -> o.renderable?.let { renderable?.add(it) } }
    }

    /** Called right after the scene starts being rendered */
    open suspend fun loadAssets() {}

    /**
     * Called when the scene is ready (all GameObjects have been initialized) and before the first game loop iteration.
     */
    open fun onSceneReady() {
        for (child in _children.values) { child.onSceneReady() }
    }

    /** Called before [update] has been called on any GameObject. Useful for preparations before GameObjects should act */
    open fun preUpdate() {
        for (child in _children.values) {
            child.preUpdate()
        }
    }

    /** Gets called on each frame. [delta] indicates the time passed since the last frame in seconds */
    open fun update(delta: Double) {

        val iterator = _children.values.iterator()
        for (obj in iterator) {
            if (obj.alive) {
                obj.update(delta)
            }
            if (!obj.alive) {
                Log.d(TAG, "Removing dead object ${obj.name}")
                iterator.remove() // Remove items via the iterator to avoid a [ConcurrentModificationException]
                obj.renderable?.let { renderable?.remove(it) }
                obj.dispose()
            }
        }
    }
    /** Called after [update] has been called on all other [GameObject]s */
    open fun lateUpdate() {
        for (child in _children.values) {
            child.lateUpdate()
        }
    }

    open fun dispose() {
        for (child in _children.values) {
            child.renderable?.let { renderable?.remove(it) }
            child.dispose()
        }
    }
}