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
        val source = response.body()?.source()?.apply { request(Long.MAX_VALUE) }
        val content = source?.buffer?.clone()?.readString(Charsets.UTF_8)
        Logger.w("response body:$content")
        return response
    }
}