package com.qds.aiagent

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ApiService(private val ipPort: String) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun healthCheck(): String {
        return try {
            // HARDCODED FOR TESTING - Always test this exact endpoint
            val url = "http://192.168.1.65:8080/api/health"
            Log.d("ApiService", "Making request to: $url")
            val request = Request.Builder()
                .url(url)
                .build()

            val response = httpClient.newCall(request).execute()
            Log.d("ApiService", "Response status: ${response.code}")
            Log.d("ApiService", "Response successful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: "No response body"
                Log.d("ApiService", "Response body: $bodyString")
                bodyString
            } else {
                "Error: Server returned ${response.code}"
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Health check failed: ${e.message}", e)
            Log.e("ApiService", "Exception type: ${e.javaClass.simpleName}")
            "Error: ${e.message}"
        }
    }
}
