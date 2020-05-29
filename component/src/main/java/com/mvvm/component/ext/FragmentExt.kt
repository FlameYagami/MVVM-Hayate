package com.mvvm.component.ext

import androidx.fragment.app.Fragment
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

fun <E> Fragment.observerEvent(liveData: LiveData<LiveDataEvent<E>>, block: (E) -> Unit) {
    liveData.observe(this, Observer { event ->
        event.getContentIfNotHandled()?.let {
            block(it)
        }
    })
}

fun Fragment.startActivityWithPair(warpPairEvent: LiveData<LiveDataEvent<WarpPair>>) {
    observerEvent(warpPairEvent) {
        context?.startActivity(it)
    }
}

fun Fragment.startActivityWithIntent(warpBundleEvent: LiveData<LiveDataEvent<WarpIntent>>) {
    observerEvent(warpBundleEvent) {
        context?.startActivity(it)
    }
}

fun Fragment.sendBroadcast(baseVm: BaseVm) {
    observerEvent(baseVm.broadcastEvent) {
        context?.sendBroadcast(it)
    }
}

fun Fragment.startActivity(baseVm: BaseVm) {
    with(baseVm) {
        startActivityWithPair(warpPairEvent)
        startActivityWithIntent(warpIntentEvent)
    }
}

fun Fragment.dialogToast(baseVm: BaseVm) {
    with(baseVm) {
        dialogToastSuccess(dialogToastSuccessEvent)
        dialogToastFailure(dialogToastFailureEvent)
        dialogToastWarning(dialogToastWarningEvent)
    }
}

fun Fragment.dialogToastSuccess(dialogToastEvent: LiveData<LiveDataEvent<Boolean>>) {
    observerEvent(dialogToastEvent) {
        childFragmentManager.apply { showDialogToast(this, ToastType.SUCCESS, it) }
    }
}

fun Fragment.dialogToastFailure(dialogToastEvent: LiveData<LiveDataEvent<Boolean>>) {
    observerEvent(dialogToastEvent) {
        childFragmentManager.apply { showDialogToast(this, ToastType.FAILURE, it) }
    }
}

fun Fragment.dialogToastWarning(dialogToastEvent: LiveData<LiveDataEvent<Triple<Boolean, String, Int?>>>) {
    observerEvent(dialogToastEvent) {
        childFragmentManager.apply { showDialogToast(this, ToastType.WARNING, it.first, it.second, it.third) }
    }
}

fun Fragment.dialogCircularProgress(baseVm: BaseVm) {
    observerEvent(baseVm.dialogCircularProgressEvent) {
        if (it.first) {
            DialogCircularProgressUtils.show(childFragmentManager, it.second)
        } else {
            DialogCircularProgressUtils.hide()
        }
    }
}