package com.mvvm.component.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*

const val DELAY_TIME_MIN = 500L
const val DELAY_TIME_DEFAULT = 1500L
const val DELAY_TIME_1S = 1000L
const val DELAY_TIME_2S = 2000L

fun <T> T.delayMain(lifecycleOwner: LifecycleOwner, delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.Main) {
    delayCoroutine(lifecycleOwner, delayTime, block)
}

fun <T> T.delayThread(lifecycleOwner: LifecycleOwner, delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.Default) {
    delayCoroutine(lifecycleOwner, delayTime, block)
}

fun <T> T.delayIO(lifecycleOwner: LifecycleOwner, delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.IO) {
    delayCoroutine(lifecycleOwner, delayTime, block)
}

suspend fun <T> T.delayCoroutine(lifecycleOwner: LifecycleOwner, delayTime: Long = 0, block: T.() -> Unit) {
    GlobalScope.launch(Dispatchers.Default) {
        delay(delayTime)
    }.takeIf {
        lifecycleOwner.lifecycle.addObserver(CoroutineLifecycle(it))
        it.join()
        !it.isCancelled
    }?.apply {
        block()
    }
}

class CoroutineLifecycle(val job: Job) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}

fun <T> T.delayMain(delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.Main) {
    delayCoroutine(delayTime, block)
}

fun <T> T.delayThread(delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.Default) {
    delayCoroutine(delayTime, block)
}

fun <T> T.delayIO(delayTime: Long = 0, block: T.() -> Unit) = GlobalScope.launch(Dispatchers.IO) {
    delayCoroutine(delayTime, block)
}

suspend fun <T> T.delayCoroutine(delayTime: Long = 0, block: T.() -> Unit) {
    GlobalScope.launch(Dispatchers.Default) {
        delay(delayTime)
    }.apply {
        join()
        block()
    }
}