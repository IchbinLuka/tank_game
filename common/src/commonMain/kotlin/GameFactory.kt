import noise.GroundNoise
import kotlin.random.Random

const val TREE_COUNT = 40

fun createGame(players: Collection<PlayerData>): GameMessageData.GameStartData {
    val heightMap = GroundNoise().allValues()
    val random = Random.Default
    val trees = List(TREE_COUNT) { random.nextDouble() * 600 - 300 }
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
        groundData = GameMessageData.GroundData(
            heightMap,
        ),
        treePositions = trees
    )
}