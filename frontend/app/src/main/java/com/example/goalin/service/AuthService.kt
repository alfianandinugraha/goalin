package com.example.goalin.service

import android.content.Context
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.repository.AuthRepository
import com.example.goalin.util.http.Http
import com.google.gson.JsonObject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AuthService(val context: Context) {
    private val repository: AuthRepository = Http
        .builder
        .build()
        .create(AuthRepository::class.java)

    suspend fun login(email: String, password: String) = coroutineScope {
        val body = JsonObject()
        body.addProperty("email", email)
        body.addProperty("password", password)

        val tokenServices = TokenService(context)

        val responseDeferred = async { repository.login(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        val token = response.body()?.payload?.token

        if (token != null) {
            tokenServices.store(token)
        }

        return@coroutineScope response.body()!!
    }

    suspend fun register(fullName: String, email: String, password: String) = coroutineScope {
        val body = JsonObject()
        body.addProperty("fullName", fullName)
        body.addProperty("email", email)
        body.addProperty("password", password)

        val tokenServices = TokenService(context)

        val responseDeferred = async { repository.register(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        val token = response.body()?.payload?.token

        if (token != null) {
            tokenServices.store(token)
        }

        return@coroutineScope response.body()!!
    }

    fun logout() {
        val tokenServices = TokenService(context)
        tokenServices.clear()
    }
}