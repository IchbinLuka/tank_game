package core.gameobjects

import ext.toFloatArray
import three.js.Object3D
import three.js.Vector3
import util.Uuid
import kotlin.math.max
import kotlin.reflect.KClass

abstract class GameObject {
    abstract val renderable: Object3D?

    /** A unique Identifier for the GameObject */
    val uuid = Uuid.next()

    /** [MutableMap] containing the relevant GameObjects */
    protected open val _children: MutableMap<String, GameObject> = hashMapOf()
    val children: Map<String, GameObject> by ::_children
    // open val _children: MutableSet<GameObject> = hashSetOf()

    protected var _health: Double = 1.0

    open val health: Double by ::_health
    open val invincible: Boolean = true

    open var position: Vector3
        get() = renderable?.position ?: Vector3()
        set(value) {
            renderable?.position?.set(*value.toFloatArray())
        }

    /** Indicates whether the object should longer be a part of the scene */
    var alive = true

    open fun damage(amount: Double) {
        if (invincible) return
        _health = max(_health - amount, 0.0)
        if (_health == 0.0) onDeath()
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

    /** Function used for preparing the GameObject. Useful for e.g. loading Assets */
    open suspend fun initVisual() {}

    override fun equals(other: Any?): Boolean = other is GameObject && other.uuid == uuid
    override fun hashCode(): Int = uuid.toInt()

    /** Used for loading things that are crucial to the scene. The scene will be shown after this completed */
    open suspend fun preloadAssets() {
        _children.values.forEach { it.initVisual() }
    }

    /** Called right after the scene starts being rendered */
    open suspend fun loadAssets() {}

    /** Gets called on each frame. [delta] indicates the time passed since the last frame in seconds */
    open fun update(delta: Double) {
        _children.values.forEach { it.update(delta) }
    }
}