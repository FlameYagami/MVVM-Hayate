package com.mvvm.component.uc.dialog

import android.content.DialogInterface
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.Job

/**
 * Created by FlameYagami on 2016/12/1.
 */

object DialogCircularProgressUtils {

    private var dialogCircularProgress: DialogCircularProgress? = null

    fun show(fragmentManager: FragmentManager, job: Job? = null, cancelable: Boolean = true) {
        hide()
        dialogCircularProgress = showDialogCircularProgress(fragmentManager, cancelable).apply {
            cancelDialogAndJob(this, job, cancelable)
        }
    }

    fun hide() {
        dialogCircularProgress?.hide()
        dialogCircularProgress = null
    }

    /**
     * 取消Dialog与相关联的Job
     */
    private fun cancelDialogAndJob(dialog: DialogCircularProgress, job: Job? = null, cancelable: Boolean) {
        if (null == job || !cancelable) return
        dialog.setDismissListener(DialogInterface.OnDismissListener {
            job.cancel()
        })
    }
}
