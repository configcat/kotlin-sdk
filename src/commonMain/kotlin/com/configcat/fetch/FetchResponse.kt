package com.configcat.fetch

import com.configcat.Entry

internal enum class FetchStatus {
    FETCHED,
    NOT_MODIFIED,
    FAILED
}

internal enum class RedirectMode {
    NO_REDIRECT,
    SHOULD_REDIRECT,
    FORCE_REDIRECT
}

internal class FetchResponse(
    status: FetchStatus,
    val entry: Entry = Entry.empty,
    val error: String? = null,
    val isTransientError: Boolean = false,
    val fetchTime: String? = null
) {
    val isFetched: Boolean = status == FetchStatus.FETCHED
    val isNotModified: Boolean = status == FetchStatus.NOT_MODIFIED
    val isFailed: Boolean = status == FetchStatus.FAILED

    companion object {
        fun success(entry: Entry, fetchTime: String?): FetchResponse = FetchResponse(FetchStatus.FETCHED, entry, fetchTime)
        fun notModified(fetchTime: String?): FetchResponse = FetchResponse(FetchStatus.NOT_MODIFIED, fetchTime = fetchTime)
        fun failure(error: String, isTransient: Boolean, fetchTime: String?): FetchResponse =
            FetchResponse(FetchStatus.FAILED, error = error, isTransientError = isTransient, fetchTime = fetchTime)
    }
}
