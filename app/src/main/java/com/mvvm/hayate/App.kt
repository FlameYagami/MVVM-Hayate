package com.mvvm.hayate

import com.mvvm.component.BaseApplication
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.koin.core.context.startKoin

class App : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        // Logger初始化
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        // Koin
        startKoin {
            // your modules
            modules(appModule)
        }
    }
}