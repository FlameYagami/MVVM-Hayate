package com.mvvm.component.uc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.mvvm.component.R
import kotlinx.android.synthetic.main.widget_appbar.view.*

/**
 * Created by FlameYagami on 2017/1/6.
 */

class AppBarView(context: Context, attrs: AttributeSet? = null) : AppBarLayout(context, attrs) {

    var onNavigationClickListener: (() -> Unit)? = null
    var onMenuClickListener: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_appbar, this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppBarView)
        val textTitle = typedArray.getString(R.styleable.AppBarView_textTitle)
        val visibleNav = typedArray.getBoolean(R.styleable.AppBarView_visibleNav, true)
        val visibleMenu = typedArray.getBoolean(R.styleable.AppBarView_visibleMenu, false)
        val visibleLogo = typedArray.getBoolean(R.styleable.AppBarView_visibleLogo, false)
        val navResId = typedArray.getResourceId(R.styleable.AppBarView_srcNav, R.mipmap.ic_nav_back)
        val menuResId = typedArray.getResourceId(R.styleable.AppBarView_srcMenu, R.mipmap.ic_nav_menu)
        typedArray.recycle()

        tvTitle.text = textTitle
        if (visibleNav) {
            imgNav.visibility = View.VISIBLE
            imgNav.setImageResource(navResId)
        }
        if (visibleMenu) {
            imgMenu.visibility = View.VISIBLE
            imgMenu.setImageResource(menuResId)
        }
        if (visibleLogo) {
            imgLogo.visibility = View.VISIBLE
        }
        imgNav.setOnClickListener { onNavigationClickListener?.invoke() }
        imgMenu.setOnClickListener { onMenuClickListener?.invoke() }
    }

    fun onNavigationClick(onNavigationClickListener: () -> Unit) {
        this.onNavigationClickListener = onNavigationClickListener
    }

    fun onMenuClick(onMenuClickListener: () -> Unit) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setMenuVisible(visible: Boolean) {
        imgMenu.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    fun setTitleText(titleText: String) {
        tvTitle.text = titleText
    }
}
