package ext

import three.js.Vector

operator fun Vector.minus(vector: Vector): Vector = addScaledVector(vector, -1)