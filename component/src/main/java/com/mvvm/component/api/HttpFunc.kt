package com.mvvm.component.api

import io.reactivex.Observable
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function

/**
 * Created by FlamYagami on 2016/8/4.
 */

class HttpFunc<T> : Function<HttpResp<T>, T> {

    override fun apply(@NonNull httpResp: HttpResp<T>): T? {
        if (httpResp.code != 200) {
            throw RuntimeException(ApiThrowable(httpResp.code.toString(), httpResp.message.toString()))
        }
        return httpResp.result
    }
}


