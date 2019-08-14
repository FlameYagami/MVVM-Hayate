package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import com.mvvm.component.R
import kotlinx.android.synthetic.main.include_material_dialog_bottom.view.*
import kotlinx.android.synthetic.main.include_material_dialog_top.view.*
import kotlinx.android.synthetic.main.material_dialog_text.view.*

class MaterialDialogText(context: Context) : MaterialDialogBase(context) {

    private var positiveListeners: ((materialDialog: MaterialDialogText) -> Unit)? = null
    private var negativeListeners: ((materialDialog: MaterialDialogText) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.material_dialog_text, this)

        btnCancel.setOnClickListener { view -> onActionButtonClicked(view as Button) }
        btnConfirm.setOnClickListener { view -> onActionButtonClicked(view as Button) }
    }

    private fun onActionButtonClicked(which: Button) {
        when (which) {
            btnCancel -> {
                negativeListeners?.invoke(this)
            }
            btnConfirm -> {
                positiveListeners?.invoke(this)
            }
        }
        if (autoDismissEnabled) {
            dismiss()
        }
    }

    fun title(@StringRes res: Int): MaterialDialogText {
        return title(context.getString(res))
    }

    fun title(res: String): MaterialDialogText {
        tvTitle.text = res
        return this
    }

    fun message(@StringRes res: Int): MaterialDialogText {
        return message(context.getString(res))
    }

    fun message(res: String): MaterialDialogText {
        tvMessage.text = res
        return this
    }

    fun positiveButton(positiveListeners: (materialDialog: MaterialDialogText) -> Unit): MaterialDialogText {
        this.positiveListeners = positiveListeners
        return this
    }

    fun negativeButton(negativeListeners: (materialDialog: MaterialDialogText) -> Unit): MaterialDialogText {
        this.negativeListeners = negativeListeners
        return this
    }

    fun negativeButtonVisible(visible: Boolean): MaterialDialogText {
        btnCancel.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun show(block: MaterialDialogText.() -> Unit) {
        this.block()
        super.show()
    }
}
