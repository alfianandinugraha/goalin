package com.example.goalin.util.http

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit as Retrofit2

class Http {
    companion object {
        private val request: Retrofit2 = Retrofit2
            .Builder()
            .baseUrl("http://192.168.43.78:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun <T> create(service: Class<T>): T {
            return request.create(service)
        }
    }
}