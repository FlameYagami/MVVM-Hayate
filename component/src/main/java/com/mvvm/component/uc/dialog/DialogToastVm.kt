package com.mvvm.component.uc.dialog

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.BaseApplication
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.R
import com.mvvm.component.utils.launchMainDelay
import com.mvvm.component.view.BaseVm

class DialogToastVm : BaseVm() {

    lateinit var dialogToastData: DialogToastData

    val imgTip = ObservableInt(-1)
    val tvTip = ObservableField("")

    val dismissDialogEvent = MutableLiveData<LiveDataEvent<Unit>>()

    override fun startViewModel() {
        when (dialogToastData.type) {
            ToastType.SUCCESS -> R.drawable.ic_dialog_success to BaseApplication.context.getString(R.string.success)
            ToastType.FAILURE -> R.drawable.ic_dialog_failure to BaseApplication.context.getString(R.string.failure)
            ToastType.WARNING -> R.drawable.ic_dialog_warming to (dialogToastData.resMessage?.let { BaseApplication.context.getString(it) }
                    ?: let { dialogToastData.strMessage })
        }.apply {
            imgTip.set(first)
            tvTip.set(second)
        }
        launchUI { launchMainDelay { dismissDialogEvent.value = LiveDataEvent(Unit) } }
    }
}