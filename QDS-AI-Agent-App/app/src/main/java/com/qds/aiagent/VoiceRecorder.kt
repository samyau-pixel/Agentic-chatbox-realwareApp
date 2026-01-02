package com.qds.aiagent

import android.app.Activity
import android.content.Intent
import android.util.Log

/**
 * VoiceRecorder handles speech-to-text conversion using RealWear's dictation API.
 * It launches the dictation intent and receives the transcribed text via activity result.
 */
class VoiceRecorder(
    private val context: Any,
    private val onVoiceResult: (String) -> Unit
) {
    private companion object {
        const val TAG = "VoiceRecorder"
        const val ACTION_DICTATION = "com.realwear.keyboard.intent.action.DICTATION"
        const val DICTATION_REQUEST_CODE = 34
        const val RESULT_KEY = "result"
    }

    private var isListening = false

    /**
     * Start listening for voice input using RealWear's dictation.
     * This launches the dictation activity which handles speech-to-text.
     */
    fun startListening() {
        if (isListening) {
            Log.w(TAG, "Voice recording already in progress")
            return
        }

        try {
            if (context !is Activity) {
                Log.e(TAG, "Context must be an Activity for dictation")
                onVoiceResult("Error: Invalid context")
                return
            }

            // Launch RealWear dictation intent
            val intent = Intent(ACTION_DICTATION)
            context.startActivityForResult(intent, DICTATION_REQUEST_CODE)
            isListening = true
            Log.d(TAG, "Started dictation")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting dictation: ${e.message}", e)
            onVoiceResult("Error: ${e.message}")
        }
    }

    /**
     * Stop listening for voice input.
     */
    fun stopListening() {
        try {
            if (isListening) {
                isListening = false
                Log.d(TAG, "Stopped dictation")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping dictation: ${e.message}", e)
        }
    }

    /**
     * Check if currently listening for voice input.
     */
    fun isRecording(): Boolean = isListening

    /**
     * Handle the result from dictation activity.
     * Should be called from MainActivity's onActivityResult.
     */
    fun handleDictationResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DICTATION_REQUEST_CODE) {
            isListening = false
            
            var result = "[Error]"
            if (resultCode == Activity.RESULT_OK && data != null) {
                result = data.getStringExtra(RESULT_KEY) ?: "[No result]"
            }
            
            Log.d(TAG, "Dictation result: $result")
            onVoiceResult(result)
        }
    }

    /**
     * Clean up resources.
     */
    fun release() {
        stopListening()
    }
}
