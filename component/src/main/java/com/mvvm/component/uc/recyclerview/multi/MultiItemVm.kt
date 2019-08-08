package com.mvvm.component.uc.recyclerview.multi

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.mvvm.component.uc.widget.RefreshStatus
import com.mvvm.component.vm.BaseVm
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class MultiItemVm<R, T> protected constructor() : BaseVm() {

    var isFooterEnabled = ObservableBoolean(false)
    var refreshStatus = ObservableField(RefreshStatus.NA)

    abstract val layoutManagerHelper: LayoutManagerHelper

    open var adapter: MultiItemAdapter<R, T> = MultiItemAdapter()
}