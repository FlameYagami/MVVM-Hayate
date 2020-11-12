package com.mvvm.component.uc.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ktx.immersionBar
import com.mvvm.component.R
import com.mvvm.component.databinding.DialogToastBinding
import com.mvvm.component.ext.observerEvent
import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.manager.AppManager
import com.mvvm.component.utils.DisplayUtils
import java.io.Serializable


/**
 * Created by 八神火焰 on 2016/12/1.
 */
class DialogToast : DialogFragment() {

    val viewModel by obtainViewModel<DialogToastVm>()

    private lateinit var binding: DialogToastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BaseDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogToastBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersionBar {
            fitsSystemWindows(true)
            statusBarColor(R.color.colorPrimary)
            navigationBarColor(R.color.colorPrimary)
            autoDarkModeEnable(true)
        }
        dialog?.apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window?.apply {
                setWindowAnimations(R.style.bottom_dialog_animation)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel.apply {
            dialogToastData = arguments?.getSerializable(DialogToastData::class.java.simpleName) as DialogToastData
            if (dialogToastData.applyNavigation) {
                val normalMargin = DisplayUtils.dp2px(16 + 16)
                val bottomMargin = DisplayUtils.dp2px(64 + 16)
                val params = binding.viewContent.layoutParams as FrameLayout.LayoutParams
                params.setMargins(normalMargin, normalMargin, normalMargin, bottomMargin)
                binding.viewContent.layoutParams = params
            }
            startViewModel()
        }

        observerEvent(viewModel.dismissDialogEvent) {
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
        show(fragmentManager, AppManager.topActivity()?.javaClass?.simpleName
                ?: DialogToast::class.java.simpleName)
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
