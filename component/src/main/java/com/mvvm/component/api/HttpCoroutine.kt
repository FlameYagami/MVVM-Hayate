package com.mvvm.component.api

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.ext.CoroutineLifecycle
import com.mvvm.component.ext.delayMain
import com.mvvm.component.view.BaseVm
import kotlinx.coroutines.*

class HttpCoroutine<T>(private val lifecycleOwner: LifecycleOwner? = null, job: suspend () -> T) {

    private val tag = HttpCoroutine::class.java.simpleName

    var onJobBefore: ((Job) -> Unit)? = null
    var onSuccessBefore: ((T) -> Unit)? = null
    var onFailureBefore: ((Throwable) -> Unit)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onFailure: ((Throwable) -> Unit)? = null
    var onCompleted: (() -> Unit)? = null

    fun onJobBefore(onJobBefore: ((Job) -> Unit)?): HttpCoroutine<T> {
        this.onJobBefore = onJobBefore
        return this
    }

    fun onSuccessBefore(onSuccessBefore: ((T) -> Unit)?): HttpCoroutine<T> {
        this.onSuccessBefore = onSuccessBefore
        return this
    }

    fun onFailureBefore(onFailureBefore: ((Throwable) -> Unit)?): HttpCoroutine<T> {
        this.onFailureBefore = onFailureBefore
        return this
    }

    fun onSuccess(onSuccess: ((T) -> Unit)?): HttpCoroutine<T> {
        this.onSuccess = onSuccess
        return this
    }

    fun onFailure(onFailure: ((Throwable) -> Unit)?): HttpCoroutine<T> {
        this.onFailure = onFailure
        return this
    }

    fun onCompleted(onCompleted: (() -> Unit)?): HttpCoroutine<T> {
        this.onCompleted = onCompleted
        return this
    }

    fun onJoin(onSuccess: ((T) -> Unit)? = null, onFailure: ((Throwable) -> Unit)? = null, onCompleted: (() -> Unit)? = null): HttpCoroutine<T> {
        this.onSuccess = onSuccess
        this.onFailure = onFailure
        this.onCompleted = onCompleted
        return this
    }

    fun applyDialog(lifecycleOwner: LifecycleOwner, baseVm: BaseVm, delayDialog: Long = 900): HttpCoroutine<T> {
        var showDialog = true
        var isFinish = false
        return onJobBefore {
            delayMain(lifecycleOwner, delayDialog) { if (showDialog && !isFinish) baseVm.dialogCircularProgressEvent.value = LiveDataEvent(true to it) }
        }.onSuccessBefore {
            showDialog = false
            isFinish = true
            delayMain(lifecycleOwner) { baseVm.dialogCircularProgressEvent.value = LiveDataEvent(false to null) }
        }.onFailureBefore {
            isFinish = true
            delayMain(lifecycleOwner) { baseVm.dialogCircularProgressEvent.value = LiveDataEvent(false to null) }
        }
    }

    init {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val deferred = GlobalScope.async(Dispatchers.IO, CoroutineStart.LAZY) { job.invoke() }
                lifecycleOwner?.lifecycle?.addObserver(CoroutineLifecycle(deferred))
                onJobBefore?.invoke(deferred)
                val result = deferred.await()
                onSuccessBefore?.invoke(result)
                onSuccess?.invoke(result)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.w(tag, "Job is cancelled")
                } else {
                    onFailureBefore?.invoke(Throwable(e))
                    onFailure?.invoke(Throwable(e))
                }
            } finally {
                onCompleted?.invoke()
            }
        }
    }
}