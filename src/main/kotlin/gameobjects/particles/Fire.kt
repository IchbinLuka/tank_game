package gameobjects.particles

import gameobjects.particles.emitter.CircleEmitter
import three.js.Vector3

class Fire(
    position: Vector3,
    radius: Double
) : BasicParticleSystem(
    particleLifetime = 10.0,
    particleLifetimeRandomness = 0.3,
    emitter = CircleEmitter(position = position, radius = radius, normal = Vector3(y = 1)),
    startDirectionRandomness = 0.6
)