import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual fun getHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }
}