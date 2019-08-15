package com.mvvm.component.api

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * OKHttp截断器
 */
class LogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Logger.w("request:$request")
        val response = chain.proceed(chain.request())
        val mediaType = response.body()?.contentType()
        val content = response.body()?.string().toString()
        Logger.w("response body:$content")
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build()
    }
}