import noise.GroundNoise

fun createGame(players: Collection<PlayerData>): GameMessageData.GameStartData {
    val heightMap = GroundNoise().allValues()
//    val ground = GameMessageData.GroundData(
//        Array(10) { i -> DoubleArray(10) { j -> (i + j).toDouble() } }
//    )
    return GameMessageData.GameStartData(
        state = GameMessageData.GameStateData(
            players.map {
                GameMessageData.TankData(
                    it,
                    GameMessageData.Vec3(0.0, 0.0, 0.0),
                    GameMessageData.Vec3(0.0, 0.0, 0.0),
                    1.0
                )
            },
            currentTurn = players.first().name
        ),
        groundData = GameMessageData.GroundData(heightMap)
    )
}