package com.mvvm.hayate.ui.profile

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.ext.onPropertyChanged
import com.mvvm.component.view.BaseVm

class FeedbackVm : BaseVm() {

    var feedbackAccount = ObservableField("")
    var description = ObservableField("")
    var descriptionEnabled = ObservableBoolean(true)
    var submitEnabled = ObservableBoolean(false)

    val submitFeedbackEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        description.onPropertyChanged { _, _ ->
            submitEnabled.set(description.get().toString().isNotEmpty())
            descriptionEnabled.set(description.get().toString().length <= 10000)
        }
    }

    /**
     * 点击事件 -> 提交
     */
    var onSubmitClick = View.OnClickListener {
        if (!descriptionEnabled.get()) return@OnClickListener
        submitFeedbackEvent.value = LiveDataEvent(Unit)
    }
}
