package com.example.goalin.util.http

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class HttpAuth {
    companion object {
        fun <T> create(context: Context, service: Class<T>): T {
            val httpClient = OkHttpClient
                .Builder()
                .addNetworkInterceptor(AuthInterceptor(context))
                .build()

            return Retrofit
                .Builder()
                .baseUrl("http://192.168.43.78:3000")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(service)
        }
    }
}