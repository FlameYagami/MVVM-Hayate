package com.mvvm.component.api

import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function

/**
 * Created by FlamYagami on 2016/8/4.
 */

class HttpFunc<T> : Function<HttpResp<T>, T> {

    override fun apply(@NonNull httpResp: HttpResp<T>): T? {
        if (httpResp.errCode != 200) {
            throw RuntimeException(ApiThrowable(httpResp.errCode.toString(), httpResp.errMsg.toString()))
        }
        return httpResp.result
    }
}


