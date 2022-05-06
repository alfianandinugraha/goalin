package com.example.goalin.util.http

import com.google.gson.Gson
import retrofit2.Response

class ApiResponseException(
    private val response: Response<*>
): Exception() {
    fun response(): ApiResponseBody<*> {
        val responseStream = response.errorBody()?.charStream()

        return Gson().fromJson(responseStream, ApiResponseBody::class.java)
    }
}