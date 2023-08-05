import com.ichbinluka.model.Player
import com.ichbinluka.model.Session
import io.ktor.websocket.*
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import sessions.SessionCreationInfo
import kotlin.test.Test

class SessionTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var webSocketSession: WebSocketSession

    @Test
    fun turnManagementTest() {
        val player1 = PlayerData("player1", ColorData(0))
        val player2 = PlayerData("player2", ColorData(1))
        val session = Session(
            creationInfo = SessionCreationInfo(
                name = "test",
                host = player1,
                password = null
            ),
            id = 1L
        )
        session.addPlayer(Player(player1, 1L, webSocketSession))
        session.addPlayer(Player(player2, 2L, webSocketSession))
        val startData = createGame(session.players.map { it.data })
        session.begin(startData)
        assert(session.currentTurn!!.data == player1)
        session.nextTurn()
        assert(session.currentTurn!!.data == player2)
        session.nextTurn()
        assert(session.currentTurn!!.data == player1)
    }

}