package com.atu.jira.users

import com.atu.jira.model.User

object UserManager {

    private var userMap: Map<String, User> = emptyMap()

    fun setUsers(users: List<User>) {
        userMap = users.associateBy { it.id }
    }

    fun getUserName(userId: String?): String {
        return userMap[userId]?.name ?: "Unknown"
    }

    fun getUser(userId: String?): User? {
        return userMap[userId]
    }

    fun isAdmin(userId: String?): Boolean {
        return userMap[userId]?.is_admin == true
    }

    fun getAllUsers(): List<User> {   // ✅ ADDED
        return userMap.values.toList()
    }


    fun isSuperAdmin(userId: String?): Boolean {
        return userMap[userId]?.is_super_admin == true
    }

    fun clear() {
        userMap = emptyMap()
    }
}