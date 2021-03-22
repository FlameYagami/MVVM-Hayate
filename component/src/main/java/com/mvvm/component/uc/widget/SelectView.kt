package com.mvvm.component.uc.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.mvvm.component.R
import com.mvvm.component.databinding.WidgetSelectBinding
import com.mvvm.component.ext.*

class SelectView(context: Context, attributeSet: AttributeSet? = null) : RelativeLayout(context, attributeSet) {

    private var subIconSize = 0

    private var _binding: WidgetSelectBinding? = null
    val binding get() = _binding!!

    init {
        _binding = WidgetSelectBinding.inflate(LayoutInflater.from(context), this)

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

        with(binding) {
            textTitle?.apply { setTitle(this) }
            textMessage?.apply { setMessage(this) }
            tvMessage.maxLines = maxLineMessage
            if (iconVisible && -1 != iconResId) {
                imgIcon.setImageResource(iconResId)
            } else {
                imgIcon.gone()
            }
            if (!arrowVisible) {
                imgRightArrow.inVisible()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val layoutParams = binding.imgSub.layoutParams as RelativeLayout.LayoutParams
        layoutParams.apply {
            width = subIconSize
            height = subIconSize
        }
        binding.imgSub.layoutParams = layoutParams
    }

    fun setTitle(message: String) {
        binding.tvTitle.text = message
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }

    fun setSubCircleImage(path: String, errorSrc: Int? = null) {
        binding.imgSub.loadCircle(path, errorSrc)
    }

    fun setSubDrawable(drawable: Drawable? = null) {
        drawable?.apply {
            binding.imgSub.background = drawable
        } ?: apply {
            binding.imgSub.clear()
        }
    }

    fun setArrowVisible(visible: Boolean) {
        if (visible) binding.imgRightArrow.visible() else binding.imgRightArrow.inVisible()
    }
}
