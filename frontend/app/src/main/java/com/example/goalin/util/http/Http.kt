package com.example.goalin.util.http

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class Http {
    companion object {
        val builder: Retrofit.Builder = Retrofit
            .Builder()
            .baseUrl("http://192.168.43.78:3000")
            .addConverterFactory(GsonConverterFactory.create())
    }
}