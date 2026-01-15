package com.qds.aiagent

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ApiService(private val ipPort: String) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(240, TimeUnit.SECONDS)  // 4 minutes for very slow backend
        .build()

    suspend fun sendMessage(question: String, history: List<List<String>> = emptyList(), historyForLlm: List<List<String>> = emptyList()): String {
        return try {
            val url = "http://$ipPort/api/chat"
            Log.d("ApiService", "Sending message to: $url")
            Log.d("ApiService", "Question: $question")
            Log.d("ApiService", "History size: ${history.size}")
            Log.d("ApiService", "HistoryForLlm size: ${historyForLlm.size}")
            
            // Create JSON request body
            val jsonBody = JSONObject()
            jsonBody.put("question", question)
            
            // Add history as array
            val historyArray = org.json.JSONArray()
            for (pair in history) {
                val pairArray = org.json.JSONArray()
                pairArray.put(pair[0])
                pairArray.put(pair[1])
                historyArray.put(pairArray)
            }
            jsonBody.put("history", historyArray)
            
            // Add historyForLlm with proper role/content structure
            val historyForLlmArray = org.json.JSONArray()
            for (pair in historyForLlm) {
                // Each pair is assumed to be [user_message, assistant_message]
                // Create user message object
                val userObj = JSONObject()
                userObj.put("role", "user")
                userObj.put("content", pair[0])
                historyForLlmArray.put(userObj)
                
                // Create assistant message object
                val assistantObj = JSONObject()
                assistantObj.put("role", "assistant")
                assistantObj.put("content", pair[1])
                historyForLlmArray.put(assistantObj)
            }
            jsonBody.put("history_for_llm", historyForLlmArray)
            
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            Log.d("ApiService", "Response status: ${response.code}")
            
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: "No response"
                Log.d("ApiService", "Response body: $bodyString")
                
                // Parse JSON response and extract answer
                val jsonResponse = JSONObject(bodyString)
                val answer = jsonResponse.optString("answer", "No answer received")
                Log.d("ApiService", "Extracted answer: $answer")
                answer
            } else {
                "Error: Server returned ${response.code}"
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Message send failed: ${e.message}", e)
            Log.e("ApiService", "Exception type: ${e.javaClass.simpleName}")
            "Error: ${e.message}"
        }
    }
}
