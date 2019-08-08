package com.mvvm.component.uc.recyclerview.sgl

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.mvvm.component.uc.widget.RefreshStatus
import com.mvvm.component.vm.BaseVm
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class SglItemVm<T> protected constructor() : BaseVm() {

    var isFooterEnabled = ObservableBoolean(false)
    var refreshStatus = ObservableField(RefreshStatus.NA)

    abstract val layoutManagerHelper: LayoutManagerHelper

    fun notifyRefreshStatus(){
        refreshStatus.set(RefreshStatus.NA)
    }

    open var adapter: SglItemAdapter<T> = SglItemAdapter()
}