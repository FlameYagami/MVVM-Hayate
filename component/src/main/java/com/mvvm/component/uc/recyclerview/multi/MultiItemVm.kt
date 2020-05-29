package com.mvvm.component.uc.recyclerview.multi

import com.mvvm.component.uc.recyclerview.BaseItemVm

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class MultiItemVm<R, T> : BaseItemVm() {
    var adapter: MultiItemAdapter<R, T> = MultiItemAdapter()
}