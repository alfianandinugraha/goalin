package com.example.goalin.service

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.model.Wallet
import com.example.goalin.repository.WalletRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WalletService(application: Application) : AndroidViewModel(application) {
    private val repository: WalletRepository = Http
        .builder
        .build()
        .create(WalletRepository::class.java)

    private val _walletsFlow = MutableSharedFlow<ResponseStatus<List<Wallet>>>(replay = 5)

    val walletsFlow = _walletsFlow.asSharedFlow()

    suspend fun getAll() = viewModelScope.launch(Dispatchers.IO) {
        _walletsFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _walletsFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _walletsFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Berhasil mendapatkan wallet"
            )
        )
    }
}