package com.mvvm.component.uc.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mvvm.component.R
import com.mvvm.component.databinding.DialogCircularProgressBinding
import java.io.Serializable

/**
 * Created by FlameYagami on 2016/12/1.
 */

class DialogCircularProgress : DialogFragment() {

    private var dialogCancelable = true
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var circularProgressDrawable: CircularProgressDrawable? = null
    private var colorResIds = arrayListOf(
        R.color.colorRed,
        R.color.colorBlue,
        R.color.colorGreen,
        R.color.colorOrange
    )

    private var _binding: DialogCircularProgressBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BaseDialog)

        val data = arguments?.getSerializable(DialogCircularProgressData::class.java.simpleName) as DialogCircularProgressData
        dialogCancelable = data.cancelable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCircularProgressBinding.inflate(LayoutInflater.from(context), container, true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCanceledOnTouchOutside(false)
            // 拦截返回键
            setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK && !dialogCancelable
            }
            // 设置自适应布局, 使DialogFragment不全屏, 规避StatusBar、NavigationBar变黑
            window?.apply { setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) }
        }

        circularProgressDrawable = CircularProgressDrawable(view.context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            val colorArray = getColorSchemeResources(view.context)
            setColorSchemeColors(*colorArray)
        }
        binding.circleImageView.setImageDrawable(circularProgressDrawable)
        circularProgressDrawable?.start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    private fun getColorSchemeResources(context: Context): IntArray {
        return IntArray(colorResIds.size).apply {
            for (i in colorResIds.indices) {
                this[i] = ContextCompat.getColor(context, colorResIds[i])
            }
        }
    }

    fun hide() {
        circularProgressDrawable?.stop()
        if (true == dialog?.isShowing) {
            dismiss()
        }
    }
}

fun showDialogCircularProgress(
    fragmentManager: FragmentManager,
    cancelable: Boolean
): DialogCircularProgress {
    return DialogCircularProgress().apply {
        val data = DialogCircularProgressData(cancelable)
        arguments = Bundle().apply { putSerializable(DialogCircularProgressData::class.java.simpleName, data) }
        show(fragmentManager, DialogCircularProgress::class.java.simpleName)
    }
}

data class DialogCircularProgressData(
    val cancelable: Boolean
) : Serializable

