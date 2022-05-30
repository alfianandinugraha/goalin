package com.example.goalin.repository

import com.example.goalin.model.User
import com.example.goalin.util.http.ApiResponseBody
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserRepository {
    @GET("/api/user")
    suspend fun get(): Response<ApiResponseBody<User>>

    @PUT("/api/user")
    suspend fun update(@Body body: JsonObject): Response<ApiResponseBody<Any>>
}