package com.configcat.client.fetch

import com.configcat.client.Config
import com.configcat.client.Constants
import com.soywiz.klock.DateTime

internal data class Entry constructor(
    val config: Config,
    val json: String,
    val eTag: String,
    val fetchTime: DateTime,
) {
    override fun equals(other: Any?): Boolean {
        return when (val config = other as? Entry) {
            null -> false
            else -> eTag == config.eTag && json == config.json
        }
    }

    override fun hashCode(): Int {
        var result = json.hashCode()
        result = 31 * result + eTag.hashCode()
        return result
    }

    fun isEmpty(): Boolean = this == empty

    companion object {
        val empty: Entry = Entry(Config.empty, "", "", Constants.minDate)
    }
}

internal enum class FetchStatus {
    FETCHED,
    NOT_MODIFIED,
    FAILED
}

internal enum class RedirectMode {
    NO_REDIRECT,
    SHOULD_REDIRECT,
    FORCE_REDIRECT,
}

internal class FetchResponse(status: FetchStatus, private val _entry: Entry = Entry.empty) {
    val entry: Entry get() = _entry

    val isFetched: Boolean = status == FetchStatus.FETCHED
    val isNotModified: Boolean = status == FetchStatus.NOT_MODIFIED
    val isFailed: Boolean = status == FetchStatus.FAILED

    companion object {
        fun success(entry: Entry): FetchResponse = FetchResponse(FetchStatus.FETCHED, entry)
        fun notModified(): FetchResponse = FetchResponse(FetchStatus.NOT_MODIFIED)
        fun failure(): FetchResponse = FetchResponse(FetchStatus.FAILED)
    }
}
