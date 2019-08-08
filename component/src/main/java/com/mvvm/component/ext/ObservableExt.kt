package com.mvvm.component.ext

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.ObservableList

fun BaseObservable.onPropertyChanged(handler: (observable: Observable, i: Int) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: Observable, propertyId: Int) {
            handler(observable, propertyId)
        }
    })
}

fun <T> ObservableList<T>.onPropertyChanged(handler: (sender: ObservableList<T>) -> Unit) {
    addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {

        override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            handler(sender)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            handler(sender)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            handler(sender)
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
            handler(sender)
        }

        override fun onChanged(sender: ObservableList<T>) {
            handler(sender)
        }
    })
}

