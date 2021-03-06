package com.example.goalin.repository

import com.example.goalin.model.Goal
import com.example.goalin.service.GoalService
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GoalRepository {
    @POST("/api/goals")
    suspend fun store(@Body body: GoalService.CreateGoalBodyRequest): Response<ApiResponseBody<Goal>>

    @GET("/api/goals")
    suspend fun getAll(): Response<ApiResponseBody<List<Goal>>>

    @DELETE("/api/goals/{goalId}")
    suspend fun delete(@Path("goalId") goalId: String): Response<ApiResponseBody<*>>
}