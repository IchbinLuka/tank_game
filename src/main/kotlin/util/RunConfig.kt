package util

val debugLevel = RunConfig.Warning

@Suppress("unused")
enum class RunConfig {
    Debug,
    Verbose,
    Warning,
    Info,
    Error,
    None;
}
