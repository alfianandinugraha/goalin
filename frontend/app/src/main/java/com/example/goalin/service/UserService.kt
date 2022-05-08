package com.example.goalin.service

import android.content.Context
import com.example.goalin.repository.UserRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.AuthInterceptor
import com.example.goalin.util.http.Http
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UserService(val context: Context) {
    private val repository = Http
        .builder
        .client(AuthInterceptor.build(context))
        .build()
        .create(UserRepository::class.java)

    suspend fun get() = coroutineScope {
        val responseDeferred = async { repository.get() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}