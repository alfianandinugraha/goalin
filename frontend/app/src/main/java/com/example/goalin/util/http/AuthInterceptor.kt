package com.example.goalin.util.http

import android.content.Context
import com.example.goalin.service.TokenService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthInterceptor(val context: Context) : Interceptor {
    companion object {
        fun build(context: Context): OkHttpClient {
            val httpClient = OkHttpClient()

            return httpClient
                .newBuilder()
                .addInterceptor(AuthInterceptor(context))
                .build()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenService = TokenService(context)
        val token = tokenService.get()

        var request = chain.request()
        request = request
            .newBuilder()
            .addHeader("Authorization", "Bearer ${token.toString()}")
            .build()

        return chain.proceed(request)
    }
}