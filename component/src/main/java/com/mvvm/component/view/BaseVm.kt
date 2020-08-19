package com.mvvm.component.view

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.WarpIntent
import com.mvvm.component.WarpPair
import com.mvvm.component.api.applyThrowableTransform
import com.mvvm.component.ext.DELAY_TIME_2S
import com.mvvm.component.manager.AppManager
import com.mvvm.component.utils.launchMainDelay
import com.mvvm.component.warpStandBy
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

open class BaseVm : ViewModel() {

    val warpPairEvent = MutableLiveData<LiveDataEvent<WarpPair>>()
    val warpIntentEvent = MutableLiveData<LiveDataEvent<WarpIntent>>()
    val dialogToastSuccessEvent = MutableLiveData<LiveDataEvent<Boolean>>()
    val dialogToastFailureEvent = MutableLiveData<LiveDataEvent<Boolean>>()
    val dialogToastWarningEvent = MutableLiveData<LiveDataEvent<Triple<Boolean, String, Int?>>>()
    val dialogCircularProgressEvent = MutableLiveData<LiveDataEvent<Pair<Boolean, Job?>>>()
    val broadcastEvent = MutableLiveData<LiveDataEvent<Intent>>()

    fun dialogToastSuccess(applyNavigation: Boolean = false) {
        dialogToastSuccessEvent.value = LiveDataEvent(applyNavigation)
    }

    fun dialogToastFailure(applyNavigation: Boolean = false) {
        dialogToastFailureEvent.value = LiveDataEvent(applyNavigation)
    }

    fun dialogToastWarning(
        applyNavigation: Boolean = false,
        strMessage: String = "", @StringRes resMessage: Int? = null
    ) {
        dialogToastWarningEvent.value = LiveDataEvent(Triple(applyNavigation, strMessage, resMessage))
    }

    fun dialogCircularProgress(showDialog: Boolean, job: Job? = null) {
        dialogCircularProgressEvent.value = LiveDataEvent(showDialog to job)
    }

    inline fun <reified T : Activity> startActivity(vararg params: Pair<String, Any?>) {
        warpPairEvent.value = LiveDataEvent(warpStandBy<T>(*params))
    }

    inline fun <reified T : Activity> startActivity(intent: Intent) {
        warpIntentEvent.value = LiveDataEvent(warpStandBy<T>(intent))
    }

    fun sendBroadcast(intent: Intent) {
        broadcastEvent.value = LiveDataEvent(intent)
    }

    open fun startViewModel() {

    }

    open var onNavigationClick = {
        AppManager.finishTopActivity()
    }

    suspend fun finishTopActivityDelay() {
        launchMainDelay(DELAY_TIME_2S) { AppManager.finishTopActivity() }
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    fun <T> launchFlowBaseDialog(
        delayDialog: Long = 500,
        block: suspend CoroutineScope.() -> T
    ): Flow<T> {
        var isFinish = false
        val deferred = CoroutineScope(Job()).async(Dispatchers.IO, CoroutineStart.LAZY) { block() }
        return flow {
            emit(deferred.await())
        }.onStart {
            delay(delayDialog)
            if (isFinish) return@onStart
            withContext(Dispatchers.Main) { dialogCircularProgress(true, deferred) }
        }.onCompletion {
            isFinish = true
            withContext(Dispatchers.Main) { dialogCircularProgress(false, null) }
        }.flowOn(Dispatchers.IO)
    }

    fun <T> launchFlowDialog(
        applyNavigation: Boolean = false,
        delayDialog: Long = 500,
        block: suspend CoroutineScope.() -> T
    ): Flow<T> {
        var isFinish = false
        val deferred = CoroutineScope(Job()).async(Dispatchers.IO, CoroutineStart.LAZY) { block() }
        return flow {
            emit(deferred.await())
        }.onStart {
            delay(delayDialog)
            if (isFinish) return@onStart
            withContext(Dispatchers.Main) { dialogCircularProgress(true, deferred) }
        }.onCompletion {
            isFinish = true
            withContext(Dispatchers.Main) { dialogCircularProgress(false, null) }
        }.catch { throwable ->
            if (throwable is CancellationException) {
                Logger.w("Job is cancelled")
                return@catch
            }
            withContext(Dispatchers.Main) { dialogToastWarning(applyNavigation = applyNavigation, strMessage = applyThrowableTransform(throwable)) }
        }.flowOn(Dispatchers.IO)
    }

    fun <T> launchFlow(
        block: suspend () -> T
    ): Flow<T> {
        return flow {
            emit(block())
        }.flowOn(Dispatchers.IO)
    }
}