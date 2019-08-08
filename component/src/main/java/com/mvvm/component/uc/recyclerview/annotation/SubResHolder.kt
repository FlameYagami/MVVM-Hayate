package com.mvvm.component.uc.recyclerview.annotation

import androidx.annotation.LayoutRes

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubResHolder(@LayoutRes val layout: Int, val br: Int = AdapterResUtils.BR_NONE)
