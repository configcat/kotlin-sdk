package com.configcat

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Describes a polling mode configuration.
 */
public interface PollingMode {
    /**
     * Gets the current polling mode's identifier.
     * Used for analytical purposes in HTTP User-Agent headers.
     */
    public val identifier: String
}

/**
 * Describes the auto polling configuration.
 */
public data class AutoPollConfiguration(
    /**
     * The interval of how often this policy should fetch the latest configuration and refresh the cache.
     */
    public var pollingInterval: Duration = 60.seconds,
    /**
     * The maximum waiting time between initialization and the first config acquisition.
     */
    public var maxInitWaitTime: Duration = 5.seconds,
)

/**
 * Describes the lazy load polling configuration.
 */
public data class LazyLoadConfiguration(
    /**
     * The interval of how long the cache will store its value before fetching the latest from the network again.
     */
    public var cacheRefreshInterval: Duration = 60.seconds,
)

/**
 * Creates an auto polling configuration.
 */
public fun autoPoll(block: AutoPollConfiguration.() -> Unit = {}): PollingMode = AutoPollMode(AutoPollConfiguration().apply(block))

/**
 * Creates a lazy load polling configuration.
 */
public fun lazyLoad(block: LazyLoadConfiguration.() -> Unit = {}): PollingMode = LazyLoadMode(LazyLoadConfiguration().apply(block))

/**
 * Creates a manual polling configuration.
 */
public fun manualPoll(): PollingMode = ManualPollMode()

internal class AutoPollMode constructor(val configuration: AutoPollConfiguration) : PollingMode {
    override val identifier: String = "a"

    init {
        if (configuration.pollingInterval.inWholeSeconds < 1) configuration.pollingInterval = 1.seconds
        if (configuration.maxInitWaitTime.inWholeSeconds < 0) configuration.maxInitWaitTime = 0.seconds
    }
}

internal class LazyLoadMode constructor(val configuration: LazyLoadConfiguration) : PollingMode {
    override val identifier: String = "l"

    init {
        if (configuration.cacheRefreshInterval.inWholeSeconds < 1) configuration.cacheRefreshInterval = 1.seconds
    }
}

internal class ManualPollMode : PollingMode {
    override val identifier: String = "m"
}
