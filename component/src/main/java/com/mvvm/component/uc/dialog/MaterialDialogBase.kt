package com.mvvm.component.uc.dialog

import android.content.Context
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView

open class MaterialDialogBase(context: Context) : LinearLayout(context) {

    private var materialDialog: MaterialDialog? = null

    var cancelable = true
    var autoDismissEnabled = true

    fun show() {
        materialDialog = MaterialDialog(context).customView(view = this, noVerticalPadding = true).show {
            cancelOnTouchOutside(cancelable)
            cancelable(cancelable)
        }
    }

    fun dismiss() {
        materialDialog?.takeIf {
            it.isShowing
        }?.apply {
            dismiss()
        }
        materialDialog = null
    }
}