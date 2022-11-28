@file:JsModule("rust_js")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

package types.rust

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Float32Array
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise

external fun greet()

external fun init_panic_hook()

external fun value_noise(width: Number, height: Number, octaves: Number): Array<Float32Array>

external interface InitOutput {
    var memory: Memory
    var greet: () -> Unit
    var init_panic_hook: () -> Unit
    var value_noise: (a: Number, b: Number, c: Number, d: Number) -> Unit
    var __wbindgen_add_to_stack_pointer: (a: Number) -> Number
    var __wbindgen_free: (a: Number, b: Number) -> Unit
    var __wbindgen_malloc: (a: Number) -> Number
    var __wbindgen_realloc: (a: Number, b: Number, c: Number) -> Number
}

external fun initSync(module: ArrayBufferView): InitOutput

external fun initSync(module: ArrayBuffer): InitOutput

external fun initSync(module: Module): InitOutput

@JsName("default")
external fun init(module_or_path: Request = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: String = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: URL = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Response = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: ArrayBufferView = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: ArrayBuffer = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Module = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Promise<Any /* Request | String | URL | Response | ArrayBufferView | ArrayBuffer | WebAssembly.Module */> = definedExternally): Promise<InitOutput>