package com.mvvm.hayate.model.login

data class LoginResp(
    val userId: Int? = null,
    val username: String? = null,
    var nickname: String? = null,
    var sex: String? = null,
    var birthday: String? = null,
    var email: String? = null,
    var avatarPath: String? = null
)