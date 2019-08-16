package com.mvvm.component.ext


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.view.BaseVm
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by FlameYagami on 2016/8/4.
 */

fun <T> Observable<T>.applyThreadMain(lifecycle: LifecycleOwner): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
    }
}

fun <T> Observable<T>.applyThreadMain(): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyIoMain(lifecycle: LifecycleOwner): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .bindUntilEvent(lifecycle, Lifecycle.Event.ON_DESTROY)
    }
}

fun <T> Observable<T>.applyIoMain(): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.applyThread(): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
    }
}

fun <T> Observable<T>.applyIo(): Observable<T> {
    return compose { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }
}

fun <T> Observable<T>.applyDialogCircleProgress(lifecycleOwner: LifecycleOwner, baseVm: BaseVm, delayDialog: Long = 900): Observable<T> {
    return compose { upstream ->
        var showDialog = true
        var isFinish = false
        upstream.doOnSubscribe { disposable ->
            delayMain(lifecycleOwner, delayDialog) { if (showDialog && !isFinish) baseVm.dialogCircularProgressEvent.value = LiveDataEvent(true to disposable) }
        }.doOnNext {
            showDialog = false
            isFinish = true
            delayMain(lifecycleOwner) { baseVm.dialogCircularProgressEvent.value = LiveDataEvent(false to null) }
        }.doOnError {
            isFinish = true
            delayMain(lifecycleOwner) { baseVm.dialogCircularProgressEvent.value = LiveDataEvent(false to null) }
        }
    }
}

