package com.example.goalin.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.model.User
import com.example.goalin.repository.UserRepository
import com.example.goalin.util.http.AuthInterceptor
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserService(application: Application): AndroidViewModel(application) {
    private val repository = Http
        .builder
        .client(AuthInterceptor.build(application))
        .build()
        .create(UserRepository::class.java)

    private val _getDetailFlow = MutableSharedFlow<ResponseStatus<User>>(replay = 5)
    private val _updateFlow = MutableSharedFlow<ResponseStatus<Any>>(replay = 5)

    val getDetailFlow = _getDetailFlow.asSharedFlow()
    val updateFlow = _updateFlow.asSharedFlow()

    suspend fun getDetail() = viewModelScope.launch(Dispatchers.IO) {
        _getDetailFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.get() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _getDetailFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _getDetailFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Berhasil mendapatkan profile"
            )
        )
    }

    suspend fun update(fullName: String, email: String) = viewModelScope.launch(Dispatchers.IO) {
        _updateFlow.emit(ResponseStatus.Loading())

        val body = JsonObject()
        body.addProperty("fullName", fullName)
        body.addProperty("email", email)

        val responseDeferred = async { repository.update(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _updateFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _updateFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Berhasil memperbarui profile"
            )
        )
    }
}