package com.configcat.fetch

import com.configcat.Entry
import com.soywiz.klock.DateTime

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
    val fetchTime: DateTime
) {
    val isFetched: Boolean = status == FetchStatus.FETCHED
    val isNotModified: Boolean = status == FetchStatus.NOT_MODIFIED
    val isFailed: Boolean = status == FetchStatus.FAILED

    companion object {
        fun success(entry: Entry, fetchTime: DateTime): FetchResponse = FetchResponse(FetchStatus.FETCHED, entry, fetchTime = fetchTime)
        fun notModified(fetchTime: DateTime): FetchResponse = FetchResponse(FetchStatus.NOT_MODIFIED, fetchTime = fetchTime)
        fun failure(error: String, isTransient: Boolean, fetchTime: DateTime): FetchResponse =
            FetchResponse(FetchStatus.FAILED, error = error, isTransientError = isTransient, fetchTime = fetchTime)
    }
}
