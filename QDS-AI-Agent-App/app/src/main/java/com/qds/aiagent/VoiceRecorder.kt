package com.qds.aiagent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * VoiceRecorder handles speech-to-text conversion using RealWear's speech recognizer.
 * It manages voice command registration, listening for speech events, and cleaning up resources.
 */
class VoiceRecorder(
    private val context: Context,
    private val onVoiceResult: (String) -> Unit
) {
    private companion object {
        const val TAG = "VoiceRecorder"
        const val ACTION_OVERRIDE_COMMANDS = "com.realwear.wearhf.intent.action.OVERRIDE_COMMANDS"
        const val ACTION_RESTORE_COMMANDS = "com.realwear.wearhf.intent.action.RESTORE_COMMANDS"
        const val ACTION_SPEECH_EVENT = "com.realwear.wearhf.intent.action.SPEECH_EVENT"
        const val EXTRA_COMMANDS = "com.realwear.wearhf.intent.extra.COMMANDS"
        const val EXTRA_SOURCE_PACKAGE = "com.realwear.wearhf.intent.extra.SOURCE_PACKAGE"
        const val EXTRA_COMMAND = "com.realwear.wearhf.intent.extra.COMMAND"
        const val EXTRA_CONFIDENCE = "com.realwear.wearhf.intent.extra.CONFIDENCE"
    }

    private var isListening = false
    private val speechBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_SPEECH_EVENT) {
                val command = intent.getStringExtra(EXTRA_COMMAND)
                val confidence = intent.getIntExtra(EXTRA_CONFIDENCE, 0)
                
                if (command != null && !command.isEmpty()) {
                    Log.d(TAG, "Voice command received: $command (confidence: $confidence)")
                    onVoiceResult(command)
                    // After receiving a command, restore normal services
                    Handler(Looper.getMainLooper()).post {
                        stopListening()
                    }
                }
            }
        }
    }

    /**
     * Start listening for voice commands.
     * This registers the voice command listener with RealWear's speech recognizer.
     */
    fun startListening() {
        if (isListening) {
            Log.w(TAG, "Voice recording already in progress")
            return
        }

        try {
            // Register the broadcast receiver to listen for speech events
            val intentFilter = IntentFilter(ACTION_SPEECH_EVENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.registerReceiver(context, speechBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(speechBroadcastReceiver, intentFilter)
            }

            // Send OVERRIDE_COMMANDS to register voice commands
            val voiceCommands = arrayListOf<String>()
            // Add a generic command that will capture any speech
            voiceCommands.add("voice_input")
            
            val intent = Intent(ACTION_OVERRIDE_COMMANDS)
            intent.putStringArrayListExtra(EXTRA_COMMANDS, voiceCommands)
            intent.putExtra(EXTRA_SOURCE_PACKAGE, context.packageName)
            
            context.sendBroadcast(intent)
            isListening = true
            Log.d(TAG, "Started listening for voice commands")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting voice listening: ${e.message}", e)
            onVoiceResult("Error: ${e.message}")
        }
    }

    /**
     * Stop listening for voice commands and restore normal speech recognizer behavior.
     */
    fun stopListening() {
        try {
            if (isListening) {
                // Unregister the broadcast receiver
                context.unregisterReceiver(speechBroadcastReceiver)
                
                // Send RESTORE_COMMANDS to reset speech recognizer
                val intent = Intent(ACTION_RESTORE_COMMANDS)
                context.sendBroadcast(intent)
                
                isListening = false
                Log.d(TAG, "Stopped listening for voice commands")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping voice listening: ${e.message}", e)
        }
    }

    /**
     * Check if currently listening for voice input.
     */
    fun isRecording(): Boolean = isListening

    /**
     * Clean up resources.
     */
    fun release() {
        stopListening()
    }
}
