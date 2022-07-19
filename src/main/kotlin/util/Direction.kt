package util

import three.js.Vector3

enum class Direction(val vector3: Vector3) {
    Left(Vector3(-1, 0, 0)) {
        override val inverse: Direction
            get() = Right
                            },
    Right(Vector3(1, 0, 0)) {
        override val inverse: Direction
            get() = Left
                            },
    Up(Vector3(0, 1, 0)) {
        override val inverse: Direction
            get() = Down
                         },
    Down(Vector3(0, -1, 0)) {
        override val inverse: Direction
            get() = Up
    };

    abstract val inverse: Direction
}