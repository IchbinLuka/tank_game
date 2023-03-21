package util

import kotlin.reflect.KProperty

typealias PropertyChangeListener<T> = (KProperty<*>, T, T) -> Unit

interface Observable<T> {

    val observers: Iterable<PropertyChangeListener<T>>

    fun notifyObservers(property: KProperty<*>, a: T, b: T) {
        for (observer in observers) {
            observer(property, a, b)
        }
    }

    fun addListener(listener: PropertyChangeListener<T>)
    fun removeListener(listener: PropertyChangeListener<T>)
}

open class DefaultObservable<T>: Observable<T> {
    override val observers = mutableListOf<PropertyChangeListener<T>>()

    override fun addListener(listener: PropertyChangeListener<T>) {
        observers.add(listener)
    }

    override fun removeListener(listener: PropertyChangeListener<T>) {
        observers.remove(listener)
    }
}

typealias ValueListener<T> = (new: T) -> Unit

class ValueNotifier<T>(
    initialValue: T
) {
    private var _value: T = initialValue
    var value: T
        get() = _value
        set(value) {
            _value = value
            notifyListeners()
        }

    private val listeners = mutableListOf<ValueListener<T>>()

    fun addListener(listener: ValueListener<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: ValueListener<T>) {
        listeners.remove(listener)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    private fun notifyListeners() {
        for (listener in listeners) {
            listener.invoke(_value)
        }
    }
}