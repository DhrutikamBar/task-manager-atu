package com.atu.jira.repo

import kotlinx.coroutines.CancellationException
import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*

suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        block()
    } catch (e: CancellationException) {
        null
    } catch (e: Exception) {
        println(e.message)
        null
    }
}