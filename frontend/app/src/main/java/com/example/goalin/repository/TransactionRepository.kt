package com.example.goalin.repository

import com.example.goalin.model.Transaction
import com.example.goalin.service.TransactionService
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionRepository {
    @POST("/api/goals/{goalId}/transactions")
    suspend fun store(
        @Path("goalId") goalId: String,
        @Body body: TransactionService.CreateTransactionBodyRequest
    ): Response<ApiResponseBody<Transaction>>

    @GET("/api/goals/{goalId}/transactions")
    suspend fun getAll(@Path("goalId") goalId: String): Response<ApiResponseBody<List<Transaction>>>
}