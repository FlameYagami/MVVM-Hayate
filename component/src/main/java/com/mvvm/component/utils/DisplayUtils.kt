package com.mvvm.component.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.mvvm.component.BaseApplication.Companion.context

object DisplayUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)

     * @param dpValue dp
     * *
     * @return px
     */
    fun dp2px(dpValue: Int): Int {
        val scale = context.resources?.displayMetrics?.density
        return (dpValue * scale as Float + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp

     * @param pxValue 像素大小
     * *
     * @return dp
     */
    fun px2dp(pxValue: Int): Int {
        val scale = context.resources?.displayMetrics?.density
        return (pxValue / scale as Float + 0.5f).toInt()
    }

    /**
     * 获取屏幕宽度

     * @return 宽度px
     */
    val screenWidth: Int
        get() {
            val screenWidth = context.resources?.displayMetrics?.widthPixels
            return screenWidth as Int
        }

    val screenHeight: Int
        get() {
            val screenHeight = context.resources?.displayMetrics?.heightPixels
            return screenHeight as Int
        }

    fun hideKeyboard(context: Context) {
        val view = (context as Activity).window.peekDecorView()
        if (view != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
