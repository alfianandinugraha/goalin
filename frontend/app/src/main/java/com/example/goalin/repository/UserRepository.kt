package com.example.goalin.repository

import com.example.goalin.model.User
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface UserRepository {
    @GET("/api/user")
    suspend fun get(): Response<ApiResponseBody<User>>
}