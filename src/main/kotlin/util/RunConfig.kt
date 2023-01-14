package util

val debugLevel = RunConfig.Debug

enum class RunConfig() {
    Debug,
    Verbose,
    Warning,
    Info,
    Error;
}
