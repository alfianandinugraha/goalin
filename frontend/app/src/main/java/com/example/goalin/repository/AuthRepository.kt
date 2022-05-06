package com.example.goalin.repository

import com.example.goalin.model.Login
import com.example.goalin.util.http.ApiResponseBody
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRepository {
    @POST("/api/login")
    suspend fun login(@Body body: JsonObject): Response<ApiResponseBody<Login>>
}