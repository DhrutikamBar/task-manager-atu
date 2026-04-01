package com.atu.jira.utils

import androidx.compose.ui.platform.LocalViewConfiguration
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object Utils {


    fun formatDateTime(iso: String): String {
        return try {
            // ✅ Ensure timezone exists
            val fixedIso = if (!iso.contains("Z") && !iso.contains("+")) {
                iso + "Z"
            } else iso

            val instant = Instant.parse(fixedIso)

            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            val day = local.dayOfMonth
            val month = local.month.name.lowercase()
                .replaceFirstChar { it.uppercase() }
                .take(3)
            val year = local.year

            var hour = local.hour
            val minute = local.minute.toString().padStart(2, '0')
            val amPm = if (hour >= 12) "PM" else "AM"

            if (hour > 12) hour -= 12
            if (hour == 0) hour = 12

            "$day $month $year, $hour:$minute $amPm"

        } catch (e: Exception) {
            iso
        }
    }


}