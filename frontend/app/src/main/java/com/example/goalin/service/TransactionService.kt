package com.example.goalin.service

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.ResponseStatus
import com.example.goalin.model.Transaction
import com.example.goalin.repository.TransactionRepository
import com.example.goalin.util.http.AuthInterceptor
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TransactionService(application: Application) : AndroidViewModel(application) {
    data class CreateTransactionBodyRequest (
        val amount: Float,
        val walletId: String,
        val createdAt: Long,
        val goalId: String
    )

    private val repository: TransactionRepository = Http
        .builder
        .client(AuthInterceptor.build(application))
        .build()
        .create(TransactionRepository::class.java)

    private val _storeFlow = MutableSharedFlow<ResponseStatus<Transaction>>(replay = 5)
    private val _getAllFlow = MutableSharedFlow<ResponseStatus<List<Transaction>>>(replay = 5)

    val storeFlow = _storeFlow.asSharedFlow()
    val getAllFlow = _getAllFlow.asSharedFlow()

    suspend fun store(body: CreateTransactionBodyRequest) = viewModelScope.launch(Dispatchers.IO) {
        _storeFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.store(body.goalId, body) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _storeFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _storeFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Berhasil menyimpan transaksi"
            )
        )
    }

    suspend fun getAll(goalId: String) = viewModelScope.launch(Dispatchers.IO) {
        _getAllFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.getAll(goalId) }
        val response = responseDeferred.await()

        Log.d("Response", goalId)

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _getAllFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _getAllFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Berhasil mendapatkan semua transaksi"
            )
        )
    }
}