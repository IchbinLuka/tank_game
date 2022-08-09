@file:JsModule("three")
@file:JsNonModule
package three

import three.js.Blending
import three.js.DepthModes
import three.js.ToneMapping

external val DoubleSide: Int
external val FrontSide: Int
external val BackSide: Int

external val NoToneMapping: ToneMapping
external val LinearToneMapping: ToneMapping
external val ReinhardToneMapping: ToneMapping

external val AdditiveBlending: Blending

external val NeverDepth: DepthModes
external val AlwaysDepth: DepthModes
external val EqualDepth: DepthModes
external val LessDepth: DepthModes
external val LessEqualDepth: DepthModes
external val GreaterEqualDepth: DepthModes
external val GreaterDepth: DepthModes
external val NotEqualDepth: DepthModes