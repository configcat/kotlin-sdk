package com.configcat.fetch

import com.configcat.model.Entry

internal enum class FetchStatus {
    FETCHED,
    NOT_MODIFIED,
    FAILED,
}

internal enum class RedirectMode {
    NO_REDIRECT,
    SHOULD_REDIRECT,
    FORCE_REDIRECT,
}

internal class FetchResponse(
    status: FetchStatus,
    val entry: Entry = Entry.empty,
    val error: String? = null,
    val errorCode: RefreshErrorCode = RefreshErrorCode.NONE,
    val errorException: Exception? = null,
    val isTransientError: Boolean = false,
) {
    val isFetched: Boolean = status == FetchStatus.FETCHED
    val isNotModified: Boolean = status == FetchStatus.NOT_MODIFIED
    val isFailed: Boolean = status == FetchStatus.FAILED

    companion object {
        fun success(entry: Entry): FetchResponse = FetchResponse(FetchStatus.FETCHED, entry)

        fun notModified(): FetchResponse = FetchResponse(FetchStatus.NOT_MODIFIED)

        fun failure(
            error: String,
            errorCode: RefreshErrorCode,
            isTransient: Boolean,
            exception: Exception? = null,
        ): FetchResponse =
            FetchResponse(
                FetchStatus.FAILED,
                error = error,
                isTransientError = isTransient,
                errorCode = errorCode,
                errorException = exception,
            )
    }
}
