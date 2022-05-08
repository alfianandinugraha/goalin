package com.example.goalin.service

import android.content.Context
import com.example.goalin.repository.CategoryRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.Http
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CategoryService(val context: Context) {
    private val repository: CategoryRepository = Http
        .builder
        .build()
        .create(CategoryRepository::class.java)

    suspend fun getAll() = coroutineScope {
        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}