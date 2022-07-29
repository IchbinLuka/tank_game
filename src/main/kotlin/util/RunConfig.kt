package util

val debugLevel = RunConfig.Verbose

enum class RunConfig() {
    Verbose,
    Debug,
    Warning,
    Info,
    Error;
}
