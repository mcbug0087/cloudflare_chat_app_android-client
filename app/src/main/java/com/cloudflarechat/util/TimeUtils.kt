package com.cloudflarechat.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val isoFormatZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val fullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun formatTime(timeStr: String?): String {
        if (timeStr.isNullOrEmpty()) return ""
        return try {
            val date = parseIso(timeStr)
            val now = System.currentTimeMillis()
            val diff = now - date.time
            when {
                diff < 60000 -> "刚刚"
                diff < 3600000 -> "${diff / 60000}分钟前"
                else -> fullFormat.format(date)
            }
        } catch (e: Exception) {
            // 解析失败时尝试提取可读部分
            if (timeStr.length >= 16) timeStr.substring(5, 16).replace("T", " ") else timeStr
        }
    }

    private fun parseIso(timeStr: String): Date {
        return try {
            if (timeStr.contains("Z")) {
                isoFormatZ.parse(timeStr) ?: Date()
            } else {
                isoFormat.parse(timeStr.substring(0, 19)) ?: Date()
            }
        } catch (e: Exception) {
            // fallback: parse with default
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(timeStr.substring(0, 19)) ?: Date()
        }
    }
}