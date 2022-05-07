package com.example.goalin.repository

import com.example.goalin.model.Goal
import com.example.goalin.service.GoalService
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GoalRepository {
    @POST("/api/goals")
    suspend fun store(@Body body: GoalService.CreateGoalBodyRequest): Response<ApiResponseBody<Goal>>
}