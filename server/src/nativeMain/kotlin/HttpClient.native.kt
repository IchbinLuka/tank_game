import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun getHttpClient(): HttpClient {
    return HttpClient(CIO) {
        engine {
            https {}
        }
    }
}