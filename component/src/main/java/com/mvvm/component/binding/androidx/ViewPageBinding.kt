package com.mvvm.component.binding.androidx

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.viewpager.widget.ViewPager
import com.mvvm.component.view.BaseFragmentAdapter

@BindingAdapter("fragmentAdapter")
fun setDeviceFragmentAdapter(view: ViewPager, adapter: BaseFragmentAdapter) {
    view.offscreenPageLimit = adapter.count // 设置预加载防止切回空白
    view.adapter = adapter
}

@BindingAdapter("currentItem")
fun setCurrentItem(view: ViewPager, currentItem: Int) {
    if (view.currentItem != currentItem)
        view.currentItem = currentItem
}

@InverseBindingAdapter(attribute = "currentItem", event = "currentItemAttrChanged")
fun getCurrentItem(view: ViewPager): Int {
    return view.currentItem
}

@BindingAdapter("currentItemAttrChanged", requireAll = false)
fun setCurrentItemChanged(view: ViewPager, currentItemAttrChanged: InverseBindingListener?) {

    val listener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            currentItemAttrChanged?.onChange()
        }
    }
    view.addOnPageChangeListener(listener)
}