import noise.GroundNoise
import kotlin.random.Random

const val TREE_COUNT = 40

fun createGame(players: Collection<PlayerData>): GameMessageData.GameStartData {
    val heightMap = GroundNoise().allValues()
    val random = Random.Default
    val trees = List(TREE_COUNT) { GameMessageData.TreeInfo(random.nextDouble() * 600 - 300, "tree_$it") }
    return GameMessageData.GameStartData(
        state = GameMessageData.GameStateData(
            players.mapIndexed { index, it ->
                GameMessageData.TankData(
                    it,
                    GameMessageData.Vec3(x = (200 / (players.size - 1)) * index.toDouble() - 100, y = 0.0, z = 1.5),
                    GameMessageData.Vec3(0.0, 0.0, 0.0),
                    1.0,
                    GameMessageData.BulletCounts.fromMap(
                        mapOf(
                            Bullet.Normal to null,
                            Bullet.NoGravity to 5,
                            Bullet.Big to 3,
                            Bullet.Teleport to 1,
                        )
                    )
                )
            },
            currentTurn = players.first().name
        ),
        groundData = GameMessageData.GroundData(
            heightMap,
        ),
        trees = trees
    )
}