package com.mvvm.component.ext


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
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

