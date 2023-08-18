enum class Bullet(
    val damage: Double = 0.4,
    val explosionWidth: Double = 30.0
) {
    Normal,
    NoGravity,
    Big(0.7, 60.0),
    Teleport(0.0, 0.0);

    companion object // Companion object so we can add static extension functions
}