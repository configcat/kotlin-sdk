package com.configcat.fetch

import com.configcat.ConfigCatClient

/** Specifies the possible config data refresh error codes. */
public enum class RefreshErrorCode(public val code: Int) {
    /** An unexpected error occurred during the refresh operation. */
    UNEXPECTED_ERROR(-1),

    /** No error occurred (the refresh operation was successful). */
    NONE(0),

    /**
     * The refresh operation failed because the client is configured to use the `OverrideBehaviour.LocalOnly`
     * override behavior, which prevents synchronization with the external cache and making HTTP requests.
     */
    LOCAL_ONLY_CLIENT(1),

    /** The refresh operation failed because the client is in offline mode, it cannot initiate HTTP requests. */
    OFFLINE_CLIENT(3200),

    /**
     * The refresh operation failed because a HTTP response indicating an
     * invalid SDK Key was received (403 Forbidden or 404 Not Found).
     */
    INVALID_SDK_KEY(1100),

    /** The refresh operation failed because an invalid HTTP response was received (unexpected HTTP status code). */
    UNEXPECTED_HTTP_RESPONSE(1101),

    /** The refresh operation failed because the HTTP request timed out. */
    HTTP_REQUEST_TIMEOUT(1102),

    /** The refresh operation failed because the HTTP request failed (most likely, due to a local network issue). */
    HTTP_REQUEST_FAILURE(1103),

    /** The refresh operation failed because an invalid HTTP response was received (200 OK with an invalid content). */
    INVALID_HTTP_RESPONSE_CONTENT(1105),

    /** Initialization of the SDK timed out. **/
    CLIENT_INIT_TIMED_OUT(4200),
}

/**
 * Represents the result of a [ConfigCatClient.forceRefresh].
 */
public data class RefreshResult(
    public val isSuccess: Boolean,
    public val error: String?,
    public val errorCode: RefreshErrorCode,
    public val exception: Exception?,
)
