package util

val debugLevel = RunConfig.Debug

@Suppress("unused")
enum class RunConfig {
    Debug,
    Verbose,
    Warning,
    Info,
    Error,
    None;
}
