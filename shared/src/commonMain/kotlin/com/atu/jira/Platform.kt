package com.atu.jira

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform