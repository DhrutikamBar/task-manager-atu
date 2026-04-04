package com.atu.jira.utils

import com.atu.jira.utils.DateFormatter.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateFormatter {

    fun format(input: String?): String {
        if (input.isNullOrBlank()) return ""

        return try {
            // Try parsing ISO first
            val instant = Instant.parse(input)
            formatInstant(instant)

        } catch (e: Exception) {

            try {
                // Try LocalDate (YYYY-MM-DD)
                val date = LocalDate.parse(input)
                formatLocalDate(date)

            } catch (e: Exception) {

                // Fallback → return original
                input
            }
        }
    }

    private fun formatInstant(instant: Instant): String {
        val zone = TimeZone.currentSystemDefault()
        val local = instant.toLocalDateTime(zone)

        val month = local.month.name.lowercase().replaceFirstChar { it.uppercase() }

        /*** Disable date with time for now **/
        /* return "${month.take(3)} ${local.dayOfMonth}, ${local.year} • ${
             formatTime(local.hour, local.minute)
         }"*/
        /*** Return date only **/
        return "${month.take(3)} ${local.dayOfMonth}, ${local.year}"
    }

    private fun formatLocalDate(date: LocalDate): String {
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        return "${month.take(3)} ${date.dayOfMonth}, ${date.year}"
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        return "$formattedHour:${minute.toString().padStart(2, '0')} $amPm"
    }
}

fun formatRelative(input: String?): String {
    val instant = Instant.parse(input ?: return "")
    val now = Clock.System.now()

    val diff = now.epochSeconds - instant.epochSeconds

    return when {
        diff < 60 -> "Just now"
        diff < 3600 -> "${diff / 60} min ago"
        diff < 86400 -> "${diff / 3600} hr ago"
        diff < 172800 -> "Yesterday"
        else -> format(input)
    }
}