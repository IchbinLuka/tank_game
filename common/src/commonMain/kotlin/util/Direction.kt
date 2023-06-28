package util

enum class Direction(val vec3: GameMessageData.Vec3) {
    Left(GameMessageData.Vec3(-1.0, 0.0, 0.0)) {
        override val inverse: Direction
            get() = Right
    },
    Right(GameMessageData.Vec3(1.0, 0.0, 0.0)) {
        override val inverse: Direction
            get() = Left
    },
    Up(GameMessageData.Vec3(0.0, 1.0, 0.0)) {
        override val inverse: Direction
            get() = Down
    },
    Down(GameMessageData.Vec3(0.0, -1.0, 0.0)) {
        override val inverse: Direction
            get() = Up
    };

    abstract val inverse: Direction
}