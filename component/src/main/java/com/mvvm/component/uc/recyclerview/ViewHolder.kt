package com.mvvm.component.uc.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class ViewHolder(var binding: ViewDataBinding, var type: Int) : RecyclerView.ViewHolder(binding.root)