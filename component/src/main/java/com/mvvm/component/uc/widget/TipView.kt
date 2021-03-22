package com.mvvm.component.uc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mvvm.component.R
import com.mvvm.component.databinding.WidgetTipBinding
import com.mvvm.component.ext.clear
import com.mvvm.component.ext.gone
import com.mvvm.component.ext.load
import com.mvvm.component.ext.visible

class TipView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var widthImage = 0
    private var heightImage = 0

    private var _binding: WidgetTipBinding? = null
    val binding get() = _binding!!

    init {
        _binding = WidgetTipBinding.inflate(LayoutInflater.from(context), this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TipView)
        val imageTip = typedArray.getResourceId(R.styleable.TipView_imageTip, R.drawable.ic_empty_data)
        val textTip = typedArray.getResourceId(R.styleable.TipView_textTip, R.string.data_empty)
        val visibleImage = typedArray.getBoolean(R.styleable.TipView_visibleImage, true)
        val visibleText = typedArray.getBoolean(R.styleable.TipView_visibleText, true)

        widthImage = typedArray.getDimensionPixelSize(
            R.styleable.TipView_widthImage,
            context.resources.getDimensionPixelSize(R.dimen.tvWidthImage)
        )
        heightImage = typedArray.getDimensionPixelSize(
            R.styleable.TipView_heightImage,
            context.resources.getDimensionPixelSize(R.dimen.tvHeightImage)
        )
        typedArray.recycle()

        tipImage(imageTip)
        tipText(textTip)
        visibleImage(visibleImage)
        visibleText(visibleText)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        changeImageSize(widthImage, heightImage)
    }

    fun changeImageSize(tempWithImage: Int, tempHeightImage: Int) {
        val layoutParams = binding.imgTip.layoutParams as ViewGroup.LayoutParams
        layoutParams.width = tempWithImage
        layoutParams.height = tempHeightImage
        binding.imgTip.layoutParams = layoutParams
    }

    fun tipImage(res: Int) {
        if (-1 == res) binding.imgTip.clear()
        else binding.imgTip.load(res)
    }

    fun tipText(res: Int) {
        if (-1 == res) binding.tvTip.text = ""
        else binding.tvTip.text = context.getString(res)
    }

    fun visibleImage(visible: Boolean) {
        if (visible) binding.imgTip.visible() else binding.imgTip.gone()
    }

    fun visibleText(visible: Boolean) {
        if (visible) binding.tvTip.visible() else binding.tvTip.gone()
    }
}