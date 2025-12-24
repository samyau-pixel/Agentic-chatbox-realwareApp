package com.qds.aiagent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private lateinit var chatListView: ListView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var settingsButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupAdapter()
        setupListeners()
        loadSettings()
    }

    private fun initializeViews() {
        chatListView = findViewById(R.id.chatListView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun setupAdapter() {
        adapter = ChatAdapter(this, messages)
        chatListView.adapter = adapter
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            }
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        val ipPort = sharedPref.getString("ip_port", "192.168.1.65:8080") ?: "192.168.1.65:8080"
        Log.d("MainActivity", "Loaded IP:Port: $ipPort")
        Toast.makeText(this, "Server: $ipPort", Toast.LENGTH_SHORT).show()
        apiService = ApiService(ipPort)
    }

    private fun sendMessage(messageText: String) {
        if (messageText.trim().isEmpty()) return
        
        // Add user message to chat
        messages.add(ChatMessage(messageText, true, System.currentTimeMillis()))
        adapter.notifyDataSetChanged()
        chatListView.setSelection(messages.size - 1)
        
        // Clear input field
        messageInput.text.clear()

        // Send message to backend
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("MainActivity", "Sending message: $messageText")
                val answer = apiService.sendMessage(messageText)
                Log.d("MainActivity", "Received answer: $answer")
                withContext(Dispatchers.Main) {
                    // Display bot response
                    messages.add(ChatMessage(answer, false, System.currentTimeMillis()))
                    adapter.notifyDataSetChanged()
                    chatListView.setSelection(messages.size - 1)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Send message error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    messages.add(ChatMessage("Error: ${e.message}", false, System.currentTimeMillis()))
                    adapter.notifyDataSetChanged()
                    chatListView.setSelection(messages.size - 1)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload settings in case they changed
        loadSettings()
    }
}
