package util

val debugLevel = RunConfig.Info

enum class RunConfig {
    Debug,
    Verbose,
    Warning,
    Info,
    Error;
}
