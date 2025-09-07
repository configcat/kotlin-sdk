package com.configcat

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Base class for monitoring application state changes.
 * Its main use is for determining whether the SDK is allowed to use HTTP or not.
 */
public abstract class StateMonitor {
    private val lock = reentrantLock()
    private val subscriptions: MutableList<(Boolean) -> Unit> = mutableListOf()

    /**
     * Subscribes a callback to be invoked whenever the application state changes.
     *
     * @param callback A function that takes a Boolean parameter, which denotes whether the application
     * is allowed to use HTTP (`true`) or not (`false`).
     */
    public fun subscribeToStateChanges(callback: (Boolean) -> Unit) {
        lock.withLock {
            subscriptions.add(callback)
        }
    }

    /**
     * Notifies all registered subscribers that the application state has changed.
     */
    protected fun notifyStateChanged() {
        lock.withLock {
            subscriptions.forEach { it(isAllowedToUseHTTP()) }
        }
    }

    /**
     * Determines whether the SDK is permitted to use HTTP for operations.
     *
     * @return `true` if the SDK is allowed to perform HTTP requests, `false` otherwise.
     */
    public abstract fun isAllowedToUseHTTP(): Boolean

    /**
     * Releases all resources and clears any state associated with the instance.
     * This method is typically called when the instance is no longer needed to free resources
     * or unsubscribe from any notifications.
     */
    public open fun close() {
        lock.withLock {
            subscriptions.clear()
        }
    }
}
