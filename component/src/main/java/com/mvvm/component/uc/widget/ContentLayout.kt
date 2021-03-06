package com.mvvm.component.uc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mvvm.component.R
import com.mvvm.component.databinding.WidgetContentLayoutBinding
import com.mvvm.component.ext.inVisible
import com.mvvm.component.ext.visible
import com.mvvm.component.uc.refresh.RefreshLayout

class ContentLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var widthImage = 0
    private var heightImage = 0

    private var refreshListener: RefreshListener? = null

    var pageIndex = 0 // 内部变量,用于控制刷新时获取的分页页数

    private var _binding: WidgetContentLayoutBinding? = null
    val binding get() = _binding!!

    interface OnRefreshListener {
        fun onHeaderRefresh(pageIndex: Int)
        fun onFooterRefresh(pageIndex: Int)
    }

    private var listener: OnRefreshListener? = null

    init {
        _binding = WidgetContentLayoutBinding.inflate(LayoutInflater.from(context), this)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentLayout)

        val imageTip = typedArray.getResourceId(R.styleable.ContentLayout_imageTip, R.drawable.ic_empty_data)
        val textTip = typedArray.getResourceId(R.styleable.ContentLayout_textTip, R.string.data_empty)
        val visibleImage = typedArray.getBoolean(R.styleable.ContentLayout_visibleImage, true) // 默认显示空数据提示图
        val visibleText = typedArray.getBoolean(R.styleable.ContentLayout_visibleText, true) // 默认显示空数据提示语

        widthImage = typedArray.getDimensionPixelSize(R.styleable.ContentLayout_widthImage, context.resources.getDimensionPixelSize(R.dimen.tvWidthImage))
        heightImage = typedArray.getDimensionPixelSize(R.styleable.ContentLayout_heightImage, context.resources.getDimensionPixelSize(R.dimen.tvHeightImage))

        typedArray.recycle()

        with(binding) {
            tipView.tipImage(imageTip)
            tipView.tipText(textTip)
            tipView.visibleImage(visibleImage)
            tipView.visibleText(visibleText)

            refreshLayout.setHeaderColorSchemeResources(R.color.colorRed, R.color.colorBlue, R.color.colorGreen, R.color.colorOrange)
            refreshLayout.setFooterColorSchemeResources(R.color.colorRed, R.color.colorBlue, R.color.colorGreen, R.color.colorOrange)

            refreshListener = RefreshListener()
            refreshLayout.setOnRefreshListener(refreshListener)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        binding.tipView.changeImageSize(widthImage, heightImage)
    }

    inner class RefreshListener : RefreshLayout.OnRefreshListener {

        override fun onHeaderRefresh() {
            pageIndex = 0 // 下拉刷新,内部变量置0
            listener?.onHeaderRefresh(pageIndex)
        }

        override fun onFooterRefresh() {
            listener?.onFooterRefresh(pageIndex.inc()) // 上拉刷新,内部变量自增
        }
    }

    fun tipImage(res: Int) {
        binding.tipView.tipImage(res)
    }

    fun tipText(res: Int) {
        binding.tipView.tipText(res)
    }

    fun setOnRefreshListener(listener: OnRefreshListener?) {
        this.listener = listener
    }

    private fun setHeaderRefreshing(refreshing: Boolean) {
        binding.refreshLayout.isHeaderRefreshing = refreshing
    }

    private fun setFooterRefreshing(refreshing: Boolean) {
        binding.refreshLayout.isFooterRefreshing = refreshing
    }

    fun setHeaderEnabled(isHeaderEnabled: Boolean) {
        binding.refreshLayout.isHeaderEnabled = isHeaderEnabled
    }

    fun setFooterEnabled(isFooterEnabled: Boolean) {
        binding.refreshLayout.isFooterEnabled = isFooterEnabled
    }

    private fun onItemCountChanged() {
        isEnabled = if (0 == binding.recyclerView.adapter?.itemCount) {
            binding.tipView.visible()
            false
        } else {
            binding.tipView.inVisible()
            true
        }
    }

    fun setRefreshStatus(refreshStatus: RefreshStatus) {
        when (refreshStatus) {
            RefreshStatus.HEADER_REFRESHING -> setHeaderRefreshing(true)
            RefreshStatus.FOOTER_REFRESHING -> setFooterRefreshing(true)
            RefreshStatus.HEADER_SUCCESS -> {
                setHeaderRefreshing(false)
                onItemCountChanged()
            }
            RefreshStatus.HEADER_FAILURE -> {
                setHeaderRefreshing(false)
            }
            RefreshStatus.FOOTER_SUCCESS -> {
                setFooterRefreshing(false)
                onItemCountChanged()
            }
            RefreshStatus.FOOTER_FAILURE -> {
                setFooterRefreshing(false)
                pageIndex.dec() // 刷新失败,内部变量自减
            }
            RefreshStatus.NA -> {
                setHeaderRefreshing(false)
                setFooterRefreshing(false)
                onItemCountChanged()
            }
        }
    }
}

enum class RefreshStatus {
    HEADER_REFRESHING, FOOTER_REFRESHING, HEADER_SUCCESS, HEADER_FAILURE, FOOTER_SUCCESS, FOOTER_FAILURE, NA
}
