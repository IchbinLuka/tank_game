package gameobjects.particles

import gameobjects.particles.emitter.CircleEmitter
import three.js.Color
import three.js.Vector3

class Fire(
    position: Vector3,
    radius: Double
) : BasicParticleSystem(
    particleLifetime = 1300.0,
    particleLifetimeRandomness = 0.3,
    emitter = CircleEmitter(position = position, radius = radius, normal = Vector3(y = 1)),
    startDirectionRandomness = 0.6,
    startColor = Color(0xfcd125),
    endColor = Color(0xfc3a0f),
    spawnBurst = 20,
    startSize = 5.0,
    spawnDelta = 10.0,
    gravity = -10.0,
    endSize = 3.0,
) {
    override fun toString(): String {
        return "Fire(position = $position, rotation = ${renderable?.rotation})"
    }
}