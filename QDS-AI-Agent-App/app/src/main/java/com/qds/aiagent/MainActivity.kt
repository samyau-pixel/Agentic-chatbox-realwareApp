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
    private lateinit var voiceButton: Button
    private lateinit var settingsButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var apiService: ApiService
    private lateinit var voiceRecorder: VoiceRecorder

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
        voiceButton = findViewById(R.id.voiceButton)
        settingsButton = findViewById(R.id.settingsButton)
        findViewById<Button>(R.id.clearHistoryButton).setOnClickListener {
            messages.clear()
            adapter.notifyDataSetChanged()
        }
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

        voiceButton.setOnClickListener {
            handleVoiceButtonClick()
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun handleVoiceButtonClick() {
        if (voiceRecorder.isRecording()) {
            voiceRecorder.stopListening()
            voiceButton.text = "🎤"
            Toast.makeText(this, "Voice recording stopped", Toast.LENGTH_SHORT).show()
        } else {
            voiceRecorder.startListening()
            voiceButton.text = "🔴"
            Toast.makeText(this, "Listening... Speak now", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        val ipPort = sharedPref.getString("ip_port", "192.168.1.65:8080") ?: "192.168.1.65:8080"
        Log.d("MainActivity", "Loaded IP:Port: $ipPort")
        Toast.makeText(this, "Server: $ipPort", Toast.LENGTH_SHORT).show()
        apiService = ApiService(ipPort)
        
        // Initialize VoiceRecorder with callback to populate input
        voiceRecorder = VoiceRecorder(this) { voiceText ->
            runOnUiThread {
                messageInput.append(voiceText)
                voiceButton.text = "🎤"
                Log.d("MainActivity", "Voice text added to input: $voiceText")
            }
        }
    }

    private fun sendMessage(messageText: String) {
        if (messageText.trim().isEmpty()) return
        
        // Add user message to chat
        messages.add(ChatMessage(messageText, true, System.currentTimeMillis()))
        adapter.notifyDataSetChanged()
        chatListView.setSelection(messages.size - 1)
        
        // Clear input field
        messageInput.text.clear()

        // Build history from chat messages (excluding the one we just added)
        val history = mutableListOf<List<String>>()
        for (i in messages.indices step 2) {
            if (i + 1 < messages.size - 1) {  // -1 because we just added user message
                val userMsg = messages[i].text
                val botMsg = messages[i + 1].text
                history.add(listOf(userMsg, botMsg))
            }
        }

        // Send message to backend with history
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("MainActivity", "Sending message: $messageText")
                Log.d("MainActivity", "With ${history.size} history items")
                val answer = apiService.sendMessage(messageText, history)
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

    override fun onPause() {
        super.onPause()
        // Stop listening if recording when app is paused
        if (::voiceRecorder.isInitialized && voiceRecorder.isRecording()) {
            voiceRecorder.stopListening()
            voiceButton.text = "🎤"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up voice recorder resources
        if (::voiceRecorder.isInitialized) {
            voiceRecorder.release()
        }
    }
}
