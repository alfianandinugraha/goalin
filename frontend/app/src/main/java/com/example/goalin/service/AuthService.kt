package com.example.goalin.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.Login
import com.example.goalin.model.Register
import com.example.goalin.model.ResponseStatus
import com.example.goalin.repository.AuthRepository
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthService(application: Application): AndroidViewModel(application) {
    private val repository: AuthRepository = Http
        .builder
        .build()
        .create(AuthRepository::class.java)

    private val _loginFlow = MutableSharedFlow<ResponseStatus<Login>>(replay = 5)
    private val _registerFlow = MutableSharedFlow<ResponseStatus<Register>>(replay = 5)

    val loginFlow = _loginFlow.asSharedFlow()
    val registerFlow = _registerFlow.asSharedFlow()

    suspend fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _loginFlow.emit(ResponseStatus.Loading())

        val body = JsonObject()
        body.addProperty("email", email)
        body.addProperty("password", password)

        val tokenServices = TokenService(getApplication())

        val responseDeferred = async { repository.login(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _loginFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        val token = response.body()?.payload?.token

        _loginFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Login berhasil"
            )
        )

        if (token != null) {
            tokenServices.store(token)
        }
    }

    suspend fun register(fullName: String, email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _registerFlow.emit(ResponseStatus.Loading())

        val body = JsonObject()
        body.addProperty("fullName", fullName)
        body.addProperty("email", email)
        body.addProperty("password", password)

        val tokenServices = TokenService(getApplication())

        val responseDeferred = async { repository.register(body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _registerFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        val token = response.body()?.payload?.token

        _registerFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Login berhasil"
            )
        )

        if (token != null) {
            tokenServices.store(token)
        }
    }

    fun logout() {
        val tokenServices = TokenService(getApplication())
        tokenServices.clear()
    }
}