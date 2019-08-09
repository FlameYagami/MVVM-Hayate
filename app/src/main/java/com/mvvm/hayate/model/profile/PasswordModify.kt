package com.mvvm.hayate.model.profile

import androidx.databinding.ObservableField

class PasswordModify(val username: String) {
    var oldPassword = ObservableField("")
    var newPassword = ObservableField("")
    var cfmPassword = ObservableField("")
}