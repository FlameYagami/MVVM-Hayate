package com.mvvm.component.uc.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mvvm.component.R
import kotlinx.android.synthetic.main.dialog_circular_progress.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.BaseDialog)

        val data = arguments?.getSerializable(DialogCircularProgressData::class.java.simpleName) as DialogCircularProgressData
        dialogCancelable = data.cancelable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_circular_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCanceledOnTouchOutside(false)
            // 拦截返回键
            setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK && !dialogCancelable
            }

            window?.apply {
                // 解决Dialog显示后StatusBar变黑问题
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.TRANSPARENT
                    navigationBarColor = Color.TRANSPARENT
                }
            }
        }

        circularProgressDrawable = CircularProgressDrawable(view.context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            val colorArray = getColorSchemeResources(view.context)
            setColorSchemeColors(*colorArray)
        }
        circleImageView.setImageDrawable(circularProgressDrawable)
        circularProgressDrawable?.start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
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

