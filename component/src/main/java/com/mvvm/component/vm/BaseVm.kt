package com.mvvm.component.vm

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mvvm.component.*
import io.reactivex.disposables.Disposable

open class BaseVm : ViewModel() {

    val warpPairEvent = MutableLiveData<LiveDataEvent<WarpPair>>()
    val warpIntentEvent = MutableLiveData<LiveDataEvent<WarpIntent>>()
    val dialogToastSuccessEvent = MutableLiveData<LiveDataEvent<Boolean>>()
    val dialogToastFailureEvent = MutableLiveData<LiveDataEvent<Boolean>>()
    val dialogToastWarningEvent = MutableLiveData<LiveDataEvent<Triple<Boolean, String, Int?>>>()
    val dialogCircularProgressEvent = MutableLiveData<LiveDataEvent<Pair<Boolean, Disposable?>>>()
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
}