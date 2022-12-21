package com.perennial.movieapp.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.perennial.movieapp.shared.mapper.Mapper
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response

class ActionStateLiveData<IN : Any, OUT : Any>(
    private val coroutineScope: CoroutineScope,
    private val mapper: Mapper<IN, OUT>,
    fetchData: (suspend () -> Response<IN>)
) {
    private val action = MutableLiveData<Action>()
    private var data: OUT? = null // backing data

    val state = action.switchMap {
        liveData(context = coroutineScope.coroutineContext) {
            when (action.value) {
                Action.Load -> {
                    emit(UIState.Loading)
                }

                Action.Refresh -> {
                    emit(UIState.Refreshing)
                }

                Action.Retry -> {
                    emit(UIState.Retrying)
                }
                else -> {}
            }

            try {
                val response = fetchData()
                val body = response.body()
                when {
                    response.isSuccessful && body != null -> {
                        data = mapper.map(body)
                        emit(UIState.Success(data))
                    }
                    action.value == Action.Refresh -> {
                        emit(UIState.RefreshFailure(Exception()))
                    }
                    else -> {
                        emit(UIState.Failure(Exception()))
                    }
                }
            } catch (exception: Exception) {
                if (action.value == Action.Refresh) {
                    emit(UIState.RefreshFailure(Exception()))
                    data?.let {
                        // emit success with existing data
                        emit(UIState.Success<OUT>(it))
                    }
                } else {
                    emit(UIState.Failure(Exception()))
                }
            }
        }
    }

    // Helpers for triggering different actions

    fun retry() {
        action.value = Action.Retry
    }

    fun refresh() {
        action.value = Action.Refresh
    }

    fun load() {
        action.value = Action.Load
    }
}