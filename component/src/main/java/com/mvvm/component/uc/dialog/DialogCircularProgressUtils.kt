package com.mvvm.component.uc.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import io.reactivex.disposables.Disposable

/**
 * Created by FlameYagami on 2016/12/1.
 */

object DialogCircularProgressUtils {

    private var dialog: DialogCircularProgress? = null

    fun show(context: Context, disposable: Disposable? = null) {
        hide()
        val keyListener = getKeyListener(disposable)
        dialog = DialogCircularProgress(context, keyListener).apply { show() }
    }

    fun hide() {
        dialog?.takeIf { it.isShowing }?.dismiss()
        dialog = null
    }

    /**
     * 返回键的监听
     */
    private fun getKeyListener(disposable: Disposable? = null): DialogInterface.OnKeyListener? {
        return if (null == disposable) null
        else DialogInterface.OnKeyListener { _, keyCode, _ ->
            takeIf {
                keyCode == KeyEvent.KEYCODE_BACK
            }?.apply {
                hide()
                disposable.dispose()
            }
            false
        }
    }
}
