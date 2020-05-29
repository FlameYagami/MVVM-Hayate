package com.mvvm.component.ext

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

suspend fun <T> Flow<T>.collectEx(
    cause: (suspend (throwable: Throwable) -> Unit)? = null,
    emit: suspend (value: T) -> Unit
) {
    catch { throwable ->
        cause?.invoke(throwable)
    }.collect {
        emit.invoke(it)
    }
}

fun <T> Flow<T>.throttleFirst(durationMillis: Long): Flow<T> {
    var job: Job = Job().apply { complete() }
    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                collect { value ->
                    if (!job.isActive) {
                        emit(value)
                        job = launch { delay(durationMillis) }
                    }
                }
            }
        }
    }
}