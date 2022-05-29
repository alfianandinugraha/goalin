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

    val getDetailFlow = _getDetailFlow.asSharedFlow()

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
}