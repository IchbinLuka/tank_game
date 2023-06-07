import com.ichbinluka.module
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import sessions.PORT

fun main() {
    embeddedServer(CIO, port = PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}