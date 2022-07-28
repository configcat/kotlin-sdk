package com.configcat.client.log

import com.configcat.client.ConfigCatClient

/**
 * Describes a logger used by [ConfigCatClient].
 */
public interface Logger {
    /**
     * Log a [message] at level [LogLevel.ERROR].
     */
    public fun error(message: String)

    /**
     * Log a [message] at level [LogLevel.WARNING].
     */
    public fun warning(message: String)

    /**
     * Log a [message] at level [LogLevel.INFO].
     */
    public fun info(message: String)

    /**
     * Log a [message] at level [LogLevel.DEBUG].
     */
    public fun debug(message: String)
}
