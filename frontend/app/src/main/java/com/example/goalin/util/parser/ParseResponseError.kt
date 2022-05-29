package com.example.goalin.util.parser

import com.example.goalin.util.http.ApiResponseBody
import com.google.gson.Gson
import retrofit2.Response

class ParseResponseError<T>(val response: Response<ApiResponseBody<T>>) {
    private val error: ApiResponseBody<T>
        get() {
            val responseStream = response.errorBody()?.charStream()
            val err = Gson().fromJson(responseStream, ApiResponseBody::class.java)

            return err as ApiResponseBody<T>
        }

    val message: String = error.message
}