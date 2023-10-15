@file:JsModule("three/examples/jsm/postprocessing/Pass")
@file:JsNonModule

package three.addons.postprocessing

abstract external class Pass {
    open var enabled: Boolean = definedExternally
    open var needsSwap: Boolean = definedExternally
    open var clear: Boolean = definedExternally
    open var renderToScreen: Boolean = definedExternally

    open fun setSize(width: Int, height: Int)
    fun render()
}