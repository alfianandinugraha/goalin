package com.example.goalin.service

import android.content.Context
import com.example.goalin.repository.GoalRepository
import com.example.goalin.repository.TransactionRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.AuthInterceptor
import com.example.goalin.util.http.Http
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class TransactionService(val context: Context) {
    data class CreateTransactionBodyRequest (
        val amount: Float,
        val walletId: String,
        val createdAt: Long,
        val goalId: String
    )

    private val repository: TransactionRepository = Http
        .builder
        .client(AuthInterceptor.build(context))
        .build()
        .create(TransactionRepository::class.java)

    suspend fun store(body: CreateTransactionBodyRequest) = coroutineScope {
        val responseDeferred = async { repository.store(body.goalId, body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}