package gameobjects.particles

import gameobjects.particles.emitter.SphereEmitter
import three.js.Color
import three.js.Vector3
import kotlin.math.pow

class Explosion(
    position: Vector3,
    radius: Double = 2.0
) : BasicParticleSystem(
    particleLifetime = 600.0,
    spawnBurst = 25 * radius.pow(2).toInt(),
    emitter = SphereEmitter(position, radius, 0),
    startVelocity = 10.0,
    startSize = 2.0,
    endSize = 10.0,
    startColor = Color(0xfcd125),
    endColor = Color(0xfc3a0f),
    startDirectionRandomness = 0.2,
    particleLifetimeRandomness = 0.3,
    velocityRandomness = 0.3,
)