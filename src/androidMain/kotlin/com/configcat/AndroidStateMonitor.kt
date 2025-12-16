package com.configcat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import kotlin.concurrent.atomics.AtomicBoolean

/**
 * Monitors the state of an Android application and determines whether the SDK
 * is allowed to perform HTTP requests.
 *
 * @constructor Creates an instance of `AndroidStateMonitor` for the specified [context].
 * The monitor automatically registers itself with the application's lifecycle and system callbacks.
 *
 * @param context The Android [Context] used for initialization and managing resources.
 */
public class AndroidStateMonitor(
    context: Context,
) : StateMonitor(),
    android.app.Application.ActivityLifecycleCallbacks,
    ComponentCallbacks2 {
    private val inForeground = AtomicBoolean(true)
    private val networkMonitor = NetworkMonitor(context)
    private val application = context.applicationContext as android.app.Application

    init {
        networkMonitor.setNotifyStateChanged(::notifyStateChanged)
        application.registerActivityLifecycleCallbacks(this)
        application.registerComponentCallbacks(this)
        application.registerReceiver(networkMonitor, IntentFilter(AndroidConstants.CONNECTIVITY_CHANGE))
    }

    override fun isAllowedToUseHTTP(): Boolean = inForeground.load() && networkMonitor.isNetworkAvailable()

    override fun onActivityCreated(
        p0: Activity,
        p1: Bundle?,
    ) {
        if (inForeground.compareAndSet(expectedValue = false, newValue = true)) {
            notifyStateChanged()
        }
    }

    override fun onActivityStarted(p0: Activity) {
        if (inForeground.compareAndSet(expectedValue = false, newValue = true)) {
            notifyStateChanged()
        }
    }

    override fun onActivityResumed(p0: Activity) {
        if (inForeground.compareAndSet(expectedValue = false, newValue = true)) {
            notifyStateChanged()
        }
    }

    override fun onActivityDestroyed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivitySaveInstanceState(
        p0: Activity,
        p1: Bundle,
    ) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onTrimMemory(p0: Int) {
        if (p0 == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (inForeground.compareAndSet(expectedValue = true, newValue = false)) {
                notifyStateChanged()
            }
        }
    }

    override fun onConfigurationChanged(p0: Configuration) {}

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {}

    override fun close() {
        application.unregisterActivityLifecycleCallbacks(this)
        application.unregisterComponentCallbacks(this)
        application.unregisterReceiver(networkMonitor)

        networkMonitor.close()
        super.close()
    }

    private class NetworkMonitor(
        private val context: Context,
    ) : BroadcastReceiver() {
        private var notifyStateChanged: (() -> Unit)? = null

        fun setNotifyStateChanged(notifyStateChanged: () -> Unit) {
            this.notifyStateChanged = notifyStateChanged
        }

        override fun onReceive(
            p0: Context?,
            p1: Intent?,
        ) {
            if (AndroidConstants.CONNECTIVITY_CHANGE != p1?.action) return
            notifyStateChanged?.invoke()
        }

        fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                context.getSystemService(
                    Context.CONNECTIVITY_SERVICE,
                ) as android.net.ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun close() {
            notifyStateChanged = null
        }
    }
}
