package com.mvvm.component.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.WarpIntent
import com.mvvm.component.WarpPair
import com.mvvm.component.startActivity
import com.mvvm.component.uc.dialog.DialogCircularProgressUtils
import com.mvvm.component.uc.dialog.ToastType
import com.mvvm.component.uc.dialog.showDialogToast
import com.mvvm.component.view.BaseVm

fun <E> AppCompatActivity.observerEvent(liveData: LiveData<LiveDataEvent<E>>, block: (E) -> Unit) {
    liveData.observe(this, Observer { event ->
        event.getContentIfNotHandled()?.let {
            block(it)
        }
    })
}

fun AppCompatActivity.startActivity(baseVm: BaseVm) {
    with(baseVm) {
        startActivityWithPair(warpPairEvent)
        startActivityWithIntent(warpIntentEvent)
    }
}

fun AppCompatActivity.startActivityWithPair(warpPairEvent: LiveData<LiveDataEvent<WarpPair>>) {
    observerEvent(warpPairEvent) {
        startActivity(it)
    }
}

fun AppCompatActivity.startActivityWithIntent(warpBundleEvent: LiveData<LiveDataEvent<WarpIntent>>) {
    observerEvent(warpBundleEvent) {
        startActivity(it)
    }
}

fun AppCompatActivity.sendBroadcast(baseVm: BaseVm) {
    observerEvent(baseVm.broadcastEvent) {
        sendBroadcast(it)
    }
}

fun AppCompatActivity.dialogToast(baseVm: BaseVm) {
    with(baseVm) {
        dialogToastSuccess(dialogToastSuccessEvent)
        dialogToastFailure(dialogToastFailureEvent)
        dialogToastWarning(dialogToastWarningEvent)
    }
}

fun AppCompatActivity.dialogToastSuccess(dialogToastEvent: LiveData<LiveDataEvent<Boolean>>) {
    observerEvent(dialogToastEvent) {
        showDialogToast(supportFragmentManager, ToastType.SUCCESS, it)
    }
}

fun AppCompatActivity.dialogToastFailure(dialogToastEvent: LiveData<LiveDataEvent<Boolean>>) {
    observerEvent(dialogToastEvent) {
        showDialogToast(supportFragmentManager, ToastType.FAILURE, it)
    }
}

fun AppCompatActivity.dialogToastWarning(dialogToastEvent: LiveData<LiveDataEvent<Triple<Boolean, String, Int?>>>) {
    observerEvent(dialogToastEvent) {
        showDialogToast(supportFragmentManager, ToastType.WARNING, it.first, it.second, it.third)
    }
}

fun AppCompatActivity.dialogCircularProgress(baseVm: BaseVm) {
    observerEvent(baseVm.dialogCircularProgressEvent) {
        if (it.first) {
            DialogCircularProgressUtils.show(supportFragmentManager, it.second)
        } else {
            DialogCircularProgressUtils.hide()
        }
    }
}