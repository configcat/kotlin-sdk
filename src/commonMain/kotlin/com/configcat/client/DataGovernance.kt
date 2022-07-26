package com.configcat.client

/**
 * Describes the location of your feature flag and setting data within the ConfigCat CDN.
 */
public enum class DataGovernance {
    /**
     * Select this if your feature flags are published to CDN nodes only in the EU.
     */
    EU_ONLY,

    /**
     * Select this if your feature flags are published to all global CDN nodes.
     */
    GLOBAL,
}
