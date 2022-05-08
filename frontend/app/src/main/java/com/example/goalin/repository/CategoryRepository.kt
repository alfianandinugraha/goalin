package com.example.goalin.repository

import com.example.goalin.model.Category
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface CategoryRepository {
    @GET("/api/categories")
    suspend fun getAll(): Response<ApiResponseBody<List<Category>>>
}