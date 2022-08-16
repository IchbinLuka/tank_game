package types

import org.w3c.dom.ImageBitmap
import org.w3c.dom.ImageData
import kotlin.js.Promise

external fun createImageBitmap(
    image: ImageData,
    sx: Number = definedExternally,
    sy: Number = definedExternally,
    sw: Number = definedExternally,
    sh: Number = definedExternally
): Promise<ImageBitmap>