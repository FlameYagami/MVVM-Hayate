package com.mvvm.component.rxjava

import com.mvvm.component.ext.applyThread
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class IntervalRangeDisposable(private var count: Long) {

    var onNext: ((time: Long) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

    private var disposable: Disposable? = null
    private var start = 0L
    private var initialDelay = 0L
    private var period = 1000L
    private var unit = TimeUnit.MILLISECONDS

    fun setStart(start: Long) {
        this.start = start
    }

    fun setInitialDelay(initialDelay: Long) {
        this.initialDelay = initialDelay
    }

    fun setPeriod(period: Long) {
        this.period = period
    }

    fun setUnit(unit: TimeUnit) {
        this.unit = unit
    }

    fun onNext(onNext: (long: Long) -> Unit) {
        this.onNext = onNext
    }

    fun onComplete(onComplete: () -> Unit) {
        this.onComplete = onComplete
    }

    fun startDisposable() {
        disposable = Observable.intervalRange(start, count, initialDelay, period, unit)
                .applyThread()
                .subscribe {
                    when (it) {
                        count - 1 -> {
                            onNext?.invoke(it)
                            onComplete?.invoke()
                        }
                        else -> onNext?.invoke(it)
                    }
                }
    }

    fun releaseDisposable() {
        disposable?.dispose()
        disposable = null
    }
}