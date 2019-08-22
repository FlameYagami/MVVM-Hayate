package com.mvvm.component.api

import com.mvvm.component.BaseApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

abstract class BaseApi {

    companion object {
        const val DEFAULT_TIMEOUT = 5000// 默认超时时间5秒
    }

    inline val okHttpClient: OkHttpClient
        get() {
            val cacheFile = File(BaseApplication.context.externalCacheDir, "HttpCache")
            val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())

            return OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(LogInterceptor())
                .addInterceptor(CacheInterceptor())
                .cache(cache)
                .build()
        }

    inline fun <reified T> service(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(T::class.java)
    }

}
