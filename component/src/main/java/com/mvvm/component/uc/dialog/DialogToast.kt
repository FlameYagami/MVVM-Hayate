package com.mvvm.component.uc.dialog

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mvvm.component.R
import com.mvvm.component.ext.DELAY_TIME_DEFAULT
import com.mvvm.component.ext.delayMain
import com.mvvm.component.utils.DisplayUtils
import kotlinx.android.synthetic.main.dialog_toast.view.*
import java.io.Serializable


/**
 * Created by FlameYagami on 2016/12/1.
 */
class DialogToast : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BaseDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_toast, container, false).apply {
            val data = arguments?.getSerializable(DialogToastData::class.java.simpleName) as DialogToastData
            when (data.type) {
                ToastType.SUCCESS -> R.drawable.ic_dialog_success to getString(R.string.success)
                ToastType.FAILURE -> R.drawable.ic_dialog_failure to getString(R.string.failure)
                ToastType.WARNING -> R.drawable.ic_dialog_warming to (data.resMessage?.let { getString(it) } ?: let { data.strMessage })
            }.apply {
                imgTip.setImageResource(first)
                tvTip.text = second
            }
            if (data.applyNavigation) {
                val normalMargin = DisplayUtils.dp2px(16 + 16)
                val bottomMargin = DisplayUtils.dp2px(64 + 16)
                val params = viewContent.layoutParams as FrameLayout.LayoutParams
                params.setMargins(normalMargin, normalMargin, normalMargin, bottomMargin)
                viewContent.layoutParams = params
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)

            window?.apply {
                setWindowAnimations(R.style.bottom_dialog_animation)
                // 解决Dialog显示后StatusBar变黑问题
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.TRANSPARENT
                }
            }
        }
    }

    fun hide() {
        if (showsDialog) {
            dismiss()
        }
    }
}

/**
 * @param type 弹出类型(成功|失败|警告)
 * @param applyNavigation 是否有底部导航栏
 * @param strMessage type为警告类型时提示的字符串
 * @param resMessage type为警告类型时提示的字符资源
 * strMessage与resMessage优先判定resMessage
 */
fun showDialogToast(
        fragmentManager: FragmentManager,
        type: ToastType,
        applyNavigation: Boolean,
        strMessage: String = "",
        @StringRes resMessage: Int? = null
) {
    DialogToast().apply {
        val data = DialogToastData(type, applyNavigation, strMessage, resMessage)
        arguments = Bundle().apply { putSerializable(DialogToastData::class.java.simpleName, data) }
        show(fragmentManager, DialogToast::class.java.simpleName)
        delayMain(this, DELAY_TIME_DEFAULT) { hide() }
    }
}

data class DialogToastData(
        val type: ToastType,
        val applyNavigation: Boolean,
        val strMessage: String = "",
        @StringRes val resMessage: Int? = null
) : Serializable

enum class ToastType {
    SUCCESS, FAILURE, WARNING
}
