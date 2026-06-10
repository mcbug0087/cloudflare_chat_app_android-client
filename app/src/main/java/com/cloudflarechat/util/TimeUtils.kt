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
    private val displayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * 格式化时间。支持：
     * - Unix 时间戳（秒），如 1780926862 (Long / Int / Double)
     * - ISO 字符串，如 "2026-06-08T12:34:56Z"
     */
    fun formatTime(raw: Any?): String {
        if (raw == null) return ""
        return try {
            val date = when (raw) {
                is Number -> Date(raw.toLong() * 1000L)
                is String -> parseIso(raw)
                else -> return ""
            }
            val now = System.currentTimeMillis()
            val diff = now - date.time
            when {
                diff < 60000 -> "刚刚"
                diff < 3600000 -> "${diff / 60000}分钟前"
                else -> displayFormat.format(date)
            }
        } catch (e: Exception) {
            raw.toString()
        }
    }

    private fun parseIso(timeStr: String): Date {
        // 可能是时间戳字符串（纯数字）
        val ts = timeStr.toLongOrNull()
        if (ts != null && ts > 1000000000) {
            return Date(ts * 1000L)
        }
        return try {
            if (timeStr.contains("Z")) {
                isoFormatZ.parse(timeStr) ?: Date()
            } else if (timeStr.length >= 19) {
                isoFormat.parse(timeStr.substring(0, 19)) ?: Date()
            } else {
                Date()
            }
        } catch (e: Exception) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .parse(timeStr.substring(0, minOf(19, timeStr.length))) ?: Date()
        }
    }
}