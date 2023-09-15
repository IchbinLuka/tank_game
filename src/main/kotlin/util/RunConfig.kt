package util

val debugLevel = RunConfig.Error

@Suppress("unused")
enum class RunConfig {
    Debug,
    Verbose,
    Warning,
    Info,
    Error,
    None;
}
