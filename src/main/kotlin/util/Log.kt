package util

object Log {
    private fun logIfLevelOver(config: RunConfig, vararg message: Any?) {
        if (debugLevel.ordinal <= config.ordinal) {
            console.log(*message)
        }
    }

    fun d(vararg message: Any?) {
        logIfLevelOver(RunConfig.Debug, *message)
    }

    fun e(vararg message: Any?) {
        logIfLevelOver(RunConfig.Error, *message)
    }

    fun v(vararg message: Any?) {
        logIfLevelOver(RunConfig.Verbose, *message)
    }

    fun i(vararg message: Any?) {
        logIfLevelOver(RunConfig.Info, *message)
    }

    fun w(vararg message: Any?) {
        logIfLevelOver(RunConfig.Warning, *message)
    }


}