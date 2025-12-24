package com.qds.aiagent

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var ipPortInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.settings_title)

        sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)

        ipPortInput = findViewById(R.id.ipPortInput)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Load current settings
        val currentIpPort = sharedPref.getString("ip_port", "192.168.1.65:8080") ?: "192.168.1.65:8080"
        ipPortInput.setText(currentIpPort)

        setupListeners()
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveSettings()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveSettings() {
        val ipPort = ipPortInput.text.toString().trim()

        // Validate format
        if (!isValidIpPort(ipPort)) {
            Toast.makeText(this, R.string.invalid_format, Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SharedPreferences
        sharedPref.edit().apply {
            putString("ip_port", ipPort)
            apply()
        }

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun isValidIpPort(ipPort: String): Boolean {
        val parts = ipPort.split(":")
        if (parts.size != 2) return false

        val ip = parts[0]
        val port = parts[1]

        // Basic IP validation
        val ipParts = ip.split(".")
        if (ipParts.size != 4) return false
        for (part in ipParts) {
            try {
                val num = part.toInt()
                if (num < 0 || num > 255) return false
            } catch (e: Exception) {
                return false
            }
        }

        // Port validation
        return try {
            val portNum = port.toInt()
            portNum > 0 && portNum < 65536
        } catch (e: Exception) {
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
