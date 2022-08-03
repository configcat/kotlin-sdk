package com.configcat

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
    public var pollingIntervalSeconds: Int = 60,
    /**
     * The maximum waiting time between initialization and the first config acquisition in seconds.
     */
    public var maxInitWaitTimeSeconds: Int = 5,
)

/**
 * Describes the lazy load polling configuration.
 */
public data class LazyLoadConfiguration(
    /**
     * The interval of how long the cache will store its value before fetching the latest from the network again.
     */
    public var cacheRefreshIntervalSeconds: Int = 60,
)

/**
 * Creates an auto polling configuration.
 */
public fun autoPoll(block: AutoPollConfiguration.() -> Unit = {}): PollingMode =
    AutoPollMode(AutoPollConfiguration().apply(block))

/**
 * Creates a lazy load polling configuration.
 */
public fun lazyLoad(block: LazyLoadConfiguration.() -> Unit = {}): PollingMode =
    LazyLoadMode(LazyLoadConfiguration().apply(block))

/**
 * Creates a manual polling configuration.
 */
public fun manualPoll(): PollingMode = ManualPollMode()

internal class AutoPollMode constructor(val configuration: AutoPollConfiguration) : PollingMode {
    override val identifier: String = "a"

    init {
        if (configuration.pollingIntervalSeconds < 1) configuration.pollingIntervalSeconds = 1
        if (configuration.maxInitWaitTimeSeconds < 0) configuration.maxInitWaitTimeSeconds = 0
    }
}

internal class LazyLoadMode constructor(val configuration: LazyLoadConfiguration) : PollingMode {
    override val identifier: String = "l"

    init {
        if (configuration.cacheRefreshIntervalSeconds < 1) configuration.cacheRefreshIntervalSeconds = 1
    }
}

internal class ManualPollMode : PollingMode {
    override val identifier: String = "m"
}
