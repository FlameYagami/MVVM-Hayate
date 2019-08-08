package com.mvvm.component.uc.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mvvm.component.R
import kotlinx.android.synthetic.main.dialog_circular_progress.*

/**
 * Created by FlameYagami on 2016/12/1.
 */
class DialogCircularProgress(context: Context, onKeyListener: DialogInterface.OnKeyListener?) : Dialog(context, R.style.BaseDialog) {

    var circularProgressDrawable: CircularProgressDrawable? = null

    init {
        val view = View.inflate(context, R.layout.dialog_circular_progress, null)
        setContentView(view)

        circularProgressDrawable = CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            val colorArray = setColorSchemeResources(R.color.colorRed, R.color.colorBlue, R.color.colorGreen, R.color.colorOrange)
            setColorSchemeColors(*colorArray)
        }
        circleImageView.setImageDrawable(circularProgressDrawable)

        setCanceledOnTouchOutside(false)

        onKeyListener?.apply {
            setCancelable(false)
            setOnKeyListener(onKeyListener)
        } ?: apply {
            setCancelable(false)
            setOnKeyListener(null)
        }
        // 解决Dialog显示后StatusBar变黑问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun setColorSchemeResources(@ColorRes vararg colorResIds: Int): IntArray {
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i])
        }
        return colorRes
    }

    override fun show() {
        super.show()
        circularProgressDrawable?.start()
    }

    override fun dismiss() {
        circularProgressDrawable?.stop()
        super.dismiss()
    }
}
