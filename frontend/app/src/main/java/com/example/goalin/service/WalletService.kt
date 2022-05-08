package com.example.goalin.service

import android.content.Context
import com.example.goalin.repository.WalletRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.Http
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class WalletService(val context: Context) {
    private val repository: WalletRepository = Http
        .builder
        .build()
        .create(WalletRepository::class.java)

    suspend fun getAll() = coroutineScope {
        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}