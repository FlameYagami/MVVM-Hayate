package com.mvvm.component.binding.androidx

import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter

@BindingAdapter("cardViewHeight")
fun setCardViewHeight(view: CardView, height: Int) {
    val layoutParams = view.layoutParams
    layoutParams.height = height
}