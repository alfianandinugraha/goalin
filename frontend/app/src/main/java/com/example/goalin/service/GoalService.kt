package com.example.goalin.service

import android.content.Context
import com.example.goalin.repository.GoalRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.Http
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GoalService(val context: Context) {
    data class CreateGoalBodyRequest(
        val name: String,
        val categoryId: String,
        val total: Float,
        val notes: String? = null
    )

    private val repository: GoalRepository = Http
        .builder
        .build()
        .create(GoalRepository::class.java)

    suspend fun store(body: CreateGoalBodyRequest) = coroutineScope {
        val responseDeferred = async { repository.store(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }

    suspend fun getAll() = coroutineScope {
        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }

    suspend fun delete(goalId: String) = coroutineScope {
        val responseDeferred = async { repository.delete(goalId) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}