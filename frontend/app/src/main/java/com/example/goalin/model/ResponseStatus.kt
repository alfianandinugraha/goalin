package com.example.goalin.model

sealed class ResponseStatus<out T> {
    class Loading: ResponseStatus<Nothing>()

    data class Success<out R>(
        val payload: R,
        val message: String,
        val code: Int
    ): ResponseStatus<R>()

    data class Error(
        val message: String,
        val code: Int
    ): ResponseStatus<Nothing>()
}
