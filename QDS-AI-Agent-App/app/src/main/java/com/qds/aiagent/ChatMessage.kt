package com.qds.aiagent

data class ChatMessage(
    val text: String,
    val isUserMessage: Boolean,
    val timestamp: Long
)
