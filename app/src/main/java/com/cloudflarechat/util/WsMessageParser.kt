package com.cloudflarechat.util

import com.google.gson.Gson
import com.cloudflarechat.data.model.WsMessage

object WsMessageParser {
    private val gson = Gson()

    fun parse(text: String): WsMessage? {
        return try {
            gson.fromJson(text, WsMessage::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun toJson(message: WsMessage): String {
        return gson.toJson(message)
    }
}