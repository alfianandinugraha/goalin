package com.example.goalin.service

import android.content.Context
import android.content.SharedPreferences

class TokenService(val context: Context) {
    private val _key = "access_token"

    private val db: SharedPreferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE)

    fun store(token: String) {
        val editor = db.edit()

        editor.putString(_key, token).apply()
    }

    fun get(): String? {
        return db.getString(_key, null)
    }

    fun clear() {
        val editor = db.edit()

        editor.remove(_key).apply()
    }
}