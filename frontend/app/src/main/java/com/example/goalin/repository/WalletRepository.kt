package com.example.goalin.repository

import com.example.goalin.model.Wallet
import com.example.goalin.util.http.ApiResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface WalletRepository {
    @GET("/api/wallets")
    suspend fun getAll(): Response<ApiResponseBody<List<Wallet>>>
}