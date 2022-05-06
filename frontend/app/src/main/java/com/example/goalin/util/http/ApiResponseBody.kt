package com.example.goalin.util.http

data class ApiResponseBody<T> (
    val message: String,
    val payload: T
)