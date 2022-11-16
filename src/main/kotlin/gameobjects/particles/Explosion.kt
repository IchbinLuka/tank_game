package gameobjects.particles

import gameobjects.particles.emitter.SphereEmitter
import three.js.Color
import three.js.Vector3

class Explosion(
    position: Vector3
) : BasicParticleSystem(
    particleLifetime = 600.0,
    spawnBurst = 100,
    emitter = SphereEmitter(position, 2.0, 0),
    startVelocity = 10.0,
    // gravity = 50.0,
    // spawnDelta = 6.0,
    startSize = 2.0,
    endSize = 10.0,
    startColor = Color(0xfcd125),
    endColor = Color(0xfc3a0f),
    startDirectionRandomness = 0.2,
    particleLifetimeRandomness = 0.3,
    velocityRandomness = 0.3,
)