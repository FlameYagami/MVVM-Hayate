package com.mvvm.component.uc.recyclerview.base

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.mvvm.component.api.applyThrowableTransform
import com.mvvm.component.uc.widget.RefreshStatus
import com.mvvm.component.view.BaseVm
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseItemVm : BaseVm() {

	var isFooterEnabled = ObservableBoolean(false)
	var refreshStatus = ObservableField(RefreshStatus.NA)

	fun notifyRefreshStatus() {
		refreshStatus.set(RefreshStatus.NA)
	}

	fun <T> launchFlowCircleDialog(
        isHeaderRefresh: Boolean = true,
        applyNavigation: Boolean = false,
        block: suspend CoroutineScope.() -> T
    ): Flow<T> {
		val deferred = CoroutineScope(Job()).async(Dispatchers.IO, CoroutineStart.LAZY) { block() }
		return flow {
			emit(deferred.await())
		}.onStart {
			refreshStatus.set(if (isHeaderRefresh) RefreshStatus.HEADER_REFRESHING else RefreshStatus.FOOTER_REFRESHING)
		}.onCompletion { throwable ->
			throwable?.apply {
				refreshStatus.set(if (isHeaderRefresh) RefreshStatus.HEADER_FAILURE else RefreshStatus.FOOTER_FAILURE)
			} ?: apply {
				refreshStatus.set(if (isHeaderRefresh) RefreshStatus.HEADER_SUCCESS else RefreshStatus.FOOTER_SUCCESS)
			}
		}.catch { throwable ->
			if (throwable is CancellationException) {
				Logger.w("Job is cancelled")
				return@catch
			}
			withContext(Dispatchers.Main) { dialogToastWarning(applyNavigation = applyNavigation, strMessage = applyThrowableTransform(throwable)) }
		}.flowOn(Dispatchers.IO)
	}
}