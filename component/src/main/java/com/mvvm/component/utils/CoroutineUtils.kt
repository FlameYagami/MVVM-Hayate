package com.mvvm.component.utils

import com.orhanobut.logger.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.concurrent.*

const val DELAY_TIME_MIN = 500L
const val DELAY_TIME_DEFAULT = 1500L
const val DELAY_TIME_1S = 1000L
const val DELAY_TIME_2S = 2000L

suspend fun launchDefault(block: () -> Unit) {
	withContext(Dispatchers.Default) { block() }
}

suspend fun launchMain(block: () -> Unit) {
	withContext(Dispatchers.Main) { block() }
}

suspend fun launchIO(block: () -> Unit) {
	withContext(Dispatchers.IO) { block() }
}

suspend fun launchDefaultDelay(delayTime: Long = DELAY_TIME_DEFAULT, block: () -> Unit) {
	delay(delayTime)
	withContext(Dispatchers.Default) { block() }
}

suspend fun launchMainDelay(delayTime: Long = DELAY_TIME_DEFAULT, block: () -> Unit) {
	delay(delayTime)
	withContext(Dispatchers.Main) { block() }
}

suspend fun launchIODelay(delayTime: Long = DELAY_TIME_DEFAULT, block: () -> Unit) {
	delay(delayTime)
	withContext(Dispatchers.IO) { block() }
}

suspend fun launchSchedule(
    start: Long = 0,
    count: Long = 0,
    initialDelay: Long = 0,
    period: Long = 0,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    next: ((time: Long) -> Unit)? = null,
    complete: (() -> Unit)? = null
) {
	// 统一换算成毫秒级
	val initialDelayInMillis = unit.toMillis(initialDelay)
	val periodInMillis = unit.toMillis(period)
	(start until count).asFlow()
		.onStart {
			delay(initialDelayInMillis)
		}.onCompletion {
			next?.invoke(count)
			complete?.invoke()
		}.onEach {
			next?.invoke(it)
			delay(periodInMillis)
		}.catch { throwable ->
			if (throwable is CancellationException) {
				Logger.w("Job is cancelled")
				return@catch
			}
		}.collect()
}