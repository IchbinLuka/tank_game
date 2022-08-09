package core.rendering

import three.js.IUniform
import three.js.`T$8`

class UniformContainer(
    private val uniforms: MutableMap<String, IUniform> = hashMapOf()
): `T$8` {
    override fun get(uniform: String): IUniform? = uniforms[uniform]

    override fun set(uniform: String, value: IUniform) = uniforms.set(uniform, value)
}