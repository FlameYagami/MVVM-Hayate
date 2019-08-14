package com.mvvm.component.uc.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import androidx.annotation.StringRes
import com.mvvm.component.R
import kotlinx.android.synthetic.main.include_material_dialog_bottom.view.*
import kotlinx.android.synthetic.main.include_material_dialog_top.view.*
import kotlinx.android.synthetic.main.material_dialog_edit.view.*

class MaterialDialogEdit(context: Context) : MaterialDialogBase(context) {

    var messageLengthEnabled = false
    var messageMinLength = 1
    var messageMaxLength = 64

    private var positiveListeners: ((materialDialog: MaterialDialogEdit, message: String) -> Unit)? = null
    private var negativeListeners: ((materialDialog: MaterialDialogEdit) -> Unit)? = null
    private var onMessageError: ((message: String) -> String)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.material_dialog_edit, this)

        textInputEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onMessageError?.invoke(textInputEditText.text.toString().trim())?.apply {
                    textInputLayout.error = this
                    textInputLayout.isErrorEnabled = isNotBlank()
                }
            }

            override fun afterTextChanged(text: Editable?) {

            }
        })

        btnCancel.setOnClickListener { onActionButtonClicked(btnCancel) }
        btnConfirm.setOnClickListener { onActionButtonClicked(btnConfirm) }
    }

    private fun onActionButtonClicked(which: Button) {
        when (which) {
            btnCancel -> {
                negativeListeners?.invoke(this)
                true
            }
            btnConfirm -> {
                val message = textInputEditText.text.toString().trim()
                val enabled = !textInputLayout.isErrorEnabled && if (messageLengthEnabled) message.length in messageMinLength..messageMaxLength else true
                if (enabled) positiveListeners?.invoke(this, message)
                enabled
            }
            else -> false
        }.takeIf {
            it && autoDismissEnabled
        }?.apply {
            dismiss()
        }
    }

    fun title(@StringRes res: Int): MaterialDialogEdit {
        tvTitle.text = context.getString(res)
        return this
    }

    fun message(text: CharSequence): MaterialDialogEdit {
        textInputEditText.setText(text)
        textInputEditText.setSelection(text.length)
        return this
    }

    fun message(@StringRes res: Int): MaterialDialogEdit {
        return message(context.getString(res))
    }

    fun messageHint(@StringRes res: Int): MaterialDialogEdit {
        textInputEditText.hint = context.getString(res)
        return this
    }

    fun positiveButton(positiveListeners: ((materialDialog: MaterialDialogEdit, message: String) -> Unit)): MaterialDialogEdit {
        this.positiveListeners = positiveListeners
        return this
    }

    fun negativeButton(negativeListeners: (materialDialog: MaterialDialogEdit) -> Unit): MaterialDialogEdit {
        this.negativeListeners = negativeListeners
        return this
    }

    fun messageLength(maxLength: Int = 64, minLength: Int = 1): MaterialDialogEdit {
        messageMinLength = minLength
        messageMaxLength = maxLength
        messageLengthEnabled = true
        textInputLayout.isCounterEnabled = true
        textInputLayout.counterMaxLength = maxLength
        return this
    }

    fun messageError(onMessageError: (message: String) -> String): MaterialDialogEdit {
        this.onMessageError = onMessageError
        return this
    }

    fun show(block: MaterialDialogEdit.() -> Unit) {
        this.block()
        super.show()
    }
}
