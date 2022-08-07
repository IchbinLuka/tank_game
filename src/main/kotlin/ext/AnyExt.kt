package ext

val Any.TAG: String get() = this::class.simpleName ?: ""