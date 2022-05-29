package com.example.goalin.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalin.model.Category
import com.example.goalin.model.ResponseStatus
import com.example.goalin.repository.CategoryRepository
import com.example.goalin.util.http.Http
import com.example.goalin.util.parser.ParseResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CategoryService(application: Application): AndroidViewModel(application) {
    private val repository: CategoryRepository = Http
        .builder
        .build()
        .create(CategoryRepository::class.java)

    private val _getAllFlow = MutableSharedFlow<ResponseStatus<List<Category>>>(replay = 5)

    val getAllFlow = _getAllFlow.asSharedFlow()

    suspend fun getAll() = viewModelScope.launch(Dispatchers.IO) {
        _getAllFlow.emit(ResponseStatus.Loading())

        val responseDeferred = async { repository.getAll() }
        val response = responseDeferred.await()

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
                message = "Berhasil mendapatkan kategori"
            )
        )
    }
}