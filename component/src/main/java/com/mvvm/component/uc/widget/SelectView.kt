package com.mvvm.component.uc.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.mvvm.component.R
import com.mvvm.component.ext.*
import kotlinx.android.synthetic.main.widget_select.view.*

class SelectView(context: Context, attributeSet: AttributeSet? = null) : RelativeLayout(context, attributeSet) {

    private var subIconSize = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_select, this)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SelectView)
        val textTitle = typedArray.getString(R.styleable.SelectView_textTitle)
        val textMessage = typedArray.getString(R.styleable.SelectView_textMessage)
        val iconResId = typedArray.getResourceId(R.styleable.SelectView_srcIcon, -1)
        subIconSize = typedArray.getDimensionPixelSize(
            R.styleable.SelectView_sizeSubIcon,
            context.resources.getDimensionPixelSize(R.dimen.sizeSubIcon)
        )
        val iconVisible = typedArray.getBoolean(R.styleable.SelectView_visibleIcon, true)
        val arrowVisible = typedArray.getBoolean(R.styleable.SelectView_visibleArrow, true)
        val maxLineMessage = typedArray.getInt(R.styleable.SelectView_maxLineMessage, 1)
        typedArray.recycle()

        setBackgroundResource(R.drawable.selector_background_common)

        textTitle?.apply { setTitle(this) }
        textMessage?.apply { setMessage(this) }
        tvMessage.maxLines = maxLineMessage
        if (iconVisible && -1 != iconResId) {
            imgIcon.setImageResource(iconResId)
        } else {
            imgIcon.gone()
        }
        if (!arrowVisible){
            imgRightArrow.inVisible()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val layoutParams = imgSub.layoutParams as RelativeLayout.LayoutParams
        layoutParams.apply {
            width = subIconSize
            height = subIconSize
        }
        imgSub.layoutParams = layoutParams
    }

    fun setTitle(message: String) {
        tvTitle.text = message
    }

    fun setMessage(message: String) {
        tvMessage.text = message
    }

    fun setSubCircleImage(path: String, errorSrc: Int? = null) {
        imgSub.loadCircle(path, errorSrc)
    }

    fun setSubDrawable(drawable: Drawable? = null) {
        drawable?.apply {
            imgSub.background = drawable
        } ?: apply {
            imgSub.clear()
        }
    }

    fun setArrowVisible(visible: Boolean){
        if (visible) imgRightArrow.visible() else imgRightArrow.inVisible()
    }
}
