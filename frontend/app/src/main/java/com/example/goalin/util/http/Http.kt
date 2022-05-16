package com.example.goalin.util.http

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class Http {
    companion object {
        val builder: Retrofit.Builder = Retrofit
            .Builder()
            .baseUrl("https://goalin.vercel.app")
            .addConverterFactory(GsonConverterFactory.create())
    }
}