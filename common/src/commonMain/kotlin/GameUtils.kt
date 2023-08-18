import kotlin.math.max
import kotlin.math.pow

fun getBulletDamage(distance: Double, bullet: Bullet): Double = if (distance < bullet.explosionWidth) {
    (1 / max(distance.pow(2) * .05, 1.0)) * bullet.damage
} else {
    0.0
}