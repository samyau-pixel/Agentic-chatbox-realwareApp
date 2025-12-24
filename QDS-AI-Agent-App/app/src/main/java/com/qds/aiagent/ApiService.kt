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

    suspend fun healthCheck(): Boolean {
        return try {
            val url = "http://$ipPort/api/health"
            Log.d("ApiService", "Making request to: $url")
            val request = Request.Builder()
                .url(url)
                .build()

            val response = httpClient.newCall(request).execute()
            Log.d("ApiService", "Response status: ${response.code}")
            Log.d("ApiService", "Response successful: ${response.isSuccessful}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Health check failed: ${e.message}", e)
            false
        }
    }
}
