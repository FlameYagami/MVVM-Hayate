package com.mvvm.component.rxjava

import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

/**
 * Created by FlameYagami on 2018/5/16.
 */
class RetryWithDelay(private val maxRetries: Int, private val retryDelayMillis: Int) : Function<Observable<out Throwable>, Observable<*>> {
    private var retryCount: Int = 0

    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable.flatMap {
            if (++retryCount <= maxRetries) {
                return@flatMap Observable.timer(retryDelayMillis.toLong(), TimeUnit.MILLISECONDS)
            }
            Observable.error<Any>(it)
        }
    }
}
