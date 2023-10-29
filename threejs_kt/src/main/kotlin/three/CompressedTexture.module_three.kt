@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ImageData

open external class CompressedTexture(mipmaps: Array<ImageData>, width: Number, height: Number, format: CompressedPixelFormat = definedExternally, type: TextureDataType = definedExternally, mapping: Mapping = definedExternally, wrapS: Wrapping = definedExternally, wrapT: Wrapping = definedExternally, magFilter: TextureFilter = definedExternally, minFilter: TextureFilter = definedExternally, anisotropy: Number = definedExternally, encoding: TextureEncoding = definedExternally) : Texture {
    override var image: `T$12`
    override var mipmaps: Array<ImageData>
    override var flipY: Boolean
    override var generateMipmaps: Boolean
}