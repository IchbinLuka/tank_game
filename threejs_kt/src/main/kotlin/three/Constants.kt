@file:JsModule("three")
@file:JsNonModule
package three

import three.js.ToneMapping

external val DoubleSide: Int
external val FrontSide: Int
external val BackSide: Int

external val NoToneMapping: ToneMapping
external val LinearToneMapping: ToneMapping
external val ReinhardToneMapping: ToneMapping