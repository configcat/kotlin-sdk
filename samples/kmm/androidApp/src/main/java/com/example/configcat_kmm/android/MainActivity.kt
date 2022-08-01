package com.example.configcat_kmm.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.configcat_kmm.FeatureFlags
import android.widget.TextView
import com.example.configcat_kmm.Platform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val scope = MainScope()
    private val featureFlags = FeatureFlags()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Hello from ${Platform().platform}!"
        val tv2: TextView = findViewById(R.id.text_view2)
        scope.launch {
            kotlin.runCatching {
                featureFlags.isFeatureEnabled("isPOCFeatureEnabled", "configcat@example.com")
            }.onFailure {
                tv2.text = it.message
            }.onSuccess {
                val message = "isPOCFeatureEnabled: $it"
                tv2.text = message
            }
        }
    }

    override fun onDestroy() {
        featureFlags.close()
        super.onDestroy()
    }
}
