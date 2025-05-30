package com.configcat

/**
 * Defines the possible states of the internal cache.
 */
public enum class ClientCacheState {
    /**
     *  No config data is available in the internal cache.
     */
    NO_FLAG_DATA,

    /**
     * Only config data provided by local flag override is available
     * in the internal cache.
     */
    HAS_LOCAL_OVERRIDE_FLAG_DATA_ONLY,

    /**
     * Only expired config data obtained from the external cache or
     * the ConfigCat CDN is available in the internal cache.
     */
    HAS_CACHED_FLAG_DATA_ONLY,

    /**
     * Up-to-date config data obtained from the external cache or
     * the ConfigCat CDN is available in the internal cache.
     */
    HAS_UP_TO_DATE_FLAG_DATA,
}
