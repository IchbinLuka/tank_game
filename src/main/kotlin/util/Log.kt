package util

import three.js.Clock
import kotlin.js.Date

object Log {
    private fun logIfLevelOver(config: RunConfig, tag: String, message: Any) {
        if (debugLevel.ordinal <= config.ordinal) {
            console.log("${Date().toISOString()}::$tag::${config.name.uppercase()}:: $message")
        }
    }

    fun d(tag: String, message: Any) {
        logIfLevelOver(RunConfig.Debug, tag, message)
    }

    fun e(tag: String, message: Any) {
        logIfLevelOver(RunConfig.Error, tag, message)
    }

    fun v(tag: String, message: Any) {
        logIfLevelOver(RunConfig.Verbose, tag, message)
    }

    fun i(tag: String, message: Any) {
        logIfLevelOver(RunConfig.Info, tag, message)
    }

    fun w(tag: String, message: Any) {
        logIfLevelOver(RunConfig.Warning, tag, message)
    }


}