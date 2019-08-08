package com.mvvm.component

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication


open class BaseApplication : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}