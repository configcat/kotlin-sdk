package com.example.configcat_android

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.configcat.*
import com.configcat.log.LogLevel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val scope = MainScope()
    private lateinit var client: ConfigCatClient

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.client = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/HhOWfwVtZ0mb30i9wi17GQ") {
            // Use ConfigCat's shared preferences cache.
            configCache = ConfigCatPreferencesCache(this@MainActivity)

            // Info level logging helps to inspect the feature flag evaluation process.
            // Use the default Warning level to avoid too detailed logging in your application.
            logLevel = LogLevel.INFO
        }

        val tv: TextView = findViewById(R.id.text_view)

        scope.launch {
            val user = ConfigCatUser(identifier = "configcat@example.com", email = "configcat@example.com")
            tv.text = "isPOCFeatureEnabled: ${client.getValue("isPOCFeatureEnabled", false, user)}"
        }
    }

    override fun onDestroy() {
        client.close()
        super.onDestroy()
    }
}