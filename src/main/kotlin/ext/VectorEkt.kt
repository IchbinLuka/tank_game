package ext

import three.js.Vector
import three.js.Vector2

operator fun Vector.minus(vector: Vector): Vector = addScaledVector(vector, -1)

operator fun Vector2.div(divisor: Double): Vector2 = divideScalar(divisor)