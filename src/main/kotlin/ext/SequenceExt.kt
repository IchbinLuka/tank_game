package ext

// Thanks to https://stackoverflow.com/a/48007581
fun <T> Sequence<T>.repeat(): Sequence<T> = sequence { while (true) yieldAll(this@repeat) }