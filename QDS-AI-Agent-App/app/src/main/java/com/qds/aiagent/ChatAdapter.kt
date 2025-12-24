package com.qds.aiagent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    context: Context,
    private val messages: List<ChatMessage>
) : ArrayAdapter<ChatMessage>(context, 0, messages) {

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = messages[position]

        val view = convertView ?: if (message.isUserMessage) {
            LayoutInflater.from(context).inflate(R.layout.item_user_message, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_bot_message, parent, false)
        }

        view.findViewById<TextView>(R.id.messageText)!!.text = message.text
        view.findViewById<TextView>(R.id.messageTime)!!.text = dateFormat.format(Date(message.timestamp))

        return view
    }
}
