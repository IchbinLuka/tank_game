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