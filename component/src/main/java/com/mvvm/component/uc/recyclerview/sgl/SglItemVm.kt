package com.mvvm.component.uc.recyclerview.sgl

import com.mvvm.component.uc.recyclerview.BaseItemVm

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class SglItemVm<T> : BaseItemVm() {
    var adapter: SglItemAdapter<T> = SglItemAdapter()
}