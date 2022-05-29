package com.example.goalin.service

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.Goal
import com.example.goalin.model.ResponseStatus
import com.example.goalin.repository.GoalRepository
import com.example.goalin.util.http.ApiResponseException
import com.example.goalin.util.http.AuthInterceptor
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GoalService(application: Application): AndroidViewModel(application) {
    data class CreateGoalBodyRequest(
        val name: String,
        val categoryId: String,
        val total: Float,
        val notes: String? = null
    )

    private val repository: GoalRepository = Http
        .builder
        .client(AuthInterceptor.build(getApplication()))
        .build()
        .create(GoalRepository::class.java)

    private val _goalsFlow = MutableSharedFlow<ResponseStatus<List<Goal>>>(replay = 5)
    private val _storeFlow = MutableSharedFlow<ResponseStatus<Goal>>(replay = 5)

    val goalsFlow = _goalsFlow.asSharedFlow()
    val storeFlow = _storeFlow.asSharedFlow()

    suspend fun store(body: CreateGoalBodyRequest) = viewModelScope.launch(Dispatchers.IO) {
        _storeFlow.emit(ResponseStatus.Loading())
        val responseDeferred = async { repository.store(body) }
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
                message = "Berhasil menyimpan Goal"
            )
        )
    }

    suspend fun getAll() = viewModelScope.launch(Dispatchers.IO) {
        _goalsFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

        if (!response.isSuccessful) {
            val err = ParseResponseError(response)

            _goalsFlow.emit(
                ResponseStatus.Error(
                    message = err.message,
                    code = response.code(),
                )
            )
            return@launch
        }

        _goalsFlow.emit(
            ResponseStatus.Success(
                payload = response.body()?.payload!!,
                code = response.code(),
                message = "Login berhasil"
            )
        )
    }

    suspend fun delete(goalId: String) = coroutineScope {
        val responseDeferred = async { repository.delete(goalId) }
        val response = responseDeferred.await()

        if (!response.isSuccessful) throw ApiResponseException(response)

        return@coroutineScope response.body()!!
    }
}