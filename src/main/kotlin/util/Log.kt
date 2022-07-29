package util

object Log {
    private fun logIfLevelOver(config: RunConfig, tag: String, message: String) {
        if (debugLevel.ordinal <= config.ordinal) {
            console.log("$tag:${config.name.uppercase()}: $message")
        }
    }

    fun d(tag: String, message: String) {
        logIfLevelOver(RunConfig.Debug, tag, message)
    }

    fun e(tag: String, message: String) {
        logIfLevelOver(RunConfig.Error, tag, message)
    }

    fun v(tag: String, message: String) {
        logIfLevelOver(RunConfig.Verbose, tag, message)
    }

    fun i(tag: String, message: String) {
        logIfLevelOver(RunConfig.Info, tag, message)
    }

    fun w(tag: String, message: String) {
        logIfLevelOver(RunConfig.Warning, tag, message)
    }


}