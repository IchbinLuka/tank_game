package util

import kotlin.coroutines.*
import kotlin.js.Promise

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({cont.resume(it)}, {cont.resumeWithException(it)})
}

fun launch(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) {
            if (result.isSuccess) {
                result.getOrNull()?.let { resume(it) }
            } else {
                result.exceptionOrNull()?.let { resumeWithException(it) }
            }
        }
        fun resume(value: Unit) {}
        fun resumeWithException(e: Throwable) { console.log("Coroutine failed: $e") }
    })
}