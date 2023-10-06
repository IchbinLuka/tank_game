import com.ichbinluka.module
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.engine.interop.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import platform.posix.setenv
import sessions.PORT
import util.LoggingLevel

fun main(args: Array<String>) {
    val arg = ArgParser("Tank Game Server")
    val port by arg.option(ArgType.Int, fullName = "port", shortName = "p").default(PORT)
    val loggingLevel by arg.option(ArgType.Choice<LoggingLevel>(), fullName = "logging").default(LoggingLevel.WARN)

    arg.parse(args)

    setenv("KTOR_LOG_LEVEL", loggingLevel.name, 1)

    embeddedServer(CIO, port = PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}