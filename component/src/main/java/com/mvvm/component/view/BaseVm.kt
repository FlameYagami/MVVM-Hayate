package com.mvvm.component.view

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mvvm.component.*
import kotlinx.coroutines.*

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

    inline fun <reified T : Activity> startActivity(vararg params: Pair<String, Any?>) {
        warpPairEvent.value = LiveDataEvent(warpStandBy<T>(*params))
    }

    inline fun <reified T : Activity> startActivity(intent: Intent) {
        warpIntentEvent.value = LiveDataEvent(warpStandBy<T>(intent))
    }

    fun sendBroadcast(intent: Intent) {
        broadcastEvent.value = LiveDataEvent(intent)
    }

    open var onNavigationClick = {
        AppManager.finishTopActivity()
    }

    suspend fun <T> applyDialog(
        job: suspend CoroutineScope.() -> T,
        success: ((T) -> Unit)? = null,
        failure: ((Throwable) -> Unit)? = null,
        finally: (() -> Unit)? = null
    ) {
        var isFinish = false
        applyApiDialog(job, success, failure, finally, {
            delay(500)
            if (isFinish) return@applyApiDialog
            withContext(Dispatchers.Main) { dialogCircularProgressEvent.value = LiveDataEvent(true to it) }
        }, {
            isFinish = true
            withContext(Dispatchers.Main) { dialogCircularProgressEvent.value = LiveDataEvent(false to null) }
        })
    }

    private suspend fun <T> applyApiDialog(
        job: suspend CoroutineScope.() -> T,
        success: ((T) -> Unit)?,
        failure: ((Throwable) -> Unit)?,
        finally: (() -> Unit)?,
        showDialog: (suspend (Job) -> Unit)?,
        hideDialog: (suspend () -> Unit)?
    ) {
        try {
            coroutineScope {
                val deferred = async(Dispatchers.IO, CoroutineStart.LAZY) { job() }
                showDialog?.invoke(deferred)
                val result = deferred.await()
                hideDialog?.invoke()
                withContext(Dispatchers.Main) { success?.invoke(result) }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Log.w("TAG", "Job is cancelled")
            } else {
                hideDialog?.invoke()
                withContext(Dispatchers.Main) { failure?.invoke(Throwable(e)) }
            }
        } finally {
            withContext(Dispatchers.Main) { finally?.invoke() }
        }
    }
}