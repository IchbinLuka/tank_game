package ext

val Any.TAG: String get() = this::class.simpleName ?: ""

inline fun <reified T> Any.tryCast(): T? = if (this is T) this else null