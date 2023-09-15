package util

import types.debug

object Log {
    private fun shouldLog(config: RunConfig) = debugLevel.ordinal <= config.ordinal

    private fun getMessage(tag: String, message: Any?, config: RunConfig) =
        "$tag::${config.name.uppercase()}:: $message"

    fun d(tag: String, message: Any?) {
        if (shouldLog(RunConfig.Debug)) console.debug(getMessage(tag, message, RunConfig.Debug))
    }

    fun e(tag: String, message: Any?) {
        if (shouldLog(RunConfig.Error)) console.error(getMessage(tag, message, RunConfig.Error))
    }

    fun v(tag: String, message: Any?) {
        if (shouldLog(RunConfig.Verbose)) console.log(getMessage(tag, message, RunConfig.Verbose))
    }

    fun i(tag: String, message: Any?) {
        if (shouldLog(RunConfig.Info)) console.info(getMessage(tag, message, RunConfig.Info))
    }

    fun w(tag: String, message: Any?) {
        if (shouldLog(RunConfig.Warning)) console.warn(getMessage(tag, message, RunConfig.Warning))
    }
}