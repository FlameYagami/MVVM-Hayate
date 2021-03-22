package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import com.mvvm.component.databinding.IncludeMaterialDialogBottomBinding
import com.mvvm.component.databinding.IncludeMaterialDialogTopBinding
import com.mvvm.component.databinding.MaterialDialogTextBinding

class MaterialDialogText(context: Context) : MaterialDialogBase(context) {

    private var positiveListeners: ((materialDialog: MaterialDialogText) -> Unit)? = null
    private var negativeListeners: ((materialDialog: MaterialDialogText) -> Unit)? = null

    private var _binding: MaterialDialogTextBinding? = null
    private var _topBinding: IncludeMaterialDialogTopBinding? = null
    private var _bottomBinding: IncludeMaterialDialogBottomBinding? = null
    private val binding get() = _binding!!
    private val topBinding get() = _topBinding!!
    private val bottomBinding get() = _bottomBinding!!

    init {
        _binding = MaterialDialogTextBinding.inflate(LayoutInflater.from(context), this)
        _topBinding = IncludeMaterialDialogTopBinding.bind(binding.root)
        _bottomBinding = IncludeMaterialDialogBottomBinding.bind(binding.root)

        with(bottomBinding) {
            btnCancel.setOnClickListener { view -> onActionButtonClicked(view as Button) }
            btnConfirm.setOnClickListener { view -> onActionButtonClicked(view as Button) }
        }
    }

    private fun onActionButtonClicked(which: Button) {
        when (which) {
            bottomBinding.btnCancel -> {
                negativeListeners?.invoke(this)
            }
            bottomBinding.btnConfirm -> {
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
        topBinding.tvTitle.text = res
        return this
    }

    fun message(@StringRes res: Int): MaterialDialogText {
        return message(context.getString(res))
    }

    fun message(res: String): MaterialDialogText {
        binding.tvMessage.text = res
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
        bottomBinding.btnCancel.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun show(block: MaterialDialogText.() -> Unit) {
        this.block()
        super.show()
    }
}
