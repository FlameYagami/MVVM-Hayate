package com.mvvm.hayate.api

import com.mvvm.component.api.BaseApi
import com.mvvm.component.api.HttpResp
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.model.app.AppUpdateResp
import com.mvvm.hayate.model.login.LoginReq
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.model.profile.AvatarResp
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


object Api : BaseApi() {

    private val service by lazy { service<Service>(Service.BASE_URL) }

    /**
     * 登入接口
     *
     * @param http 请求封装模型
     * @return 任务列表
     */
    suspend fun login(http: LoginReq): HttpResp<LoginResp> {
        return service.login(http)
    }

    /**
     * 修改昵称接口
     *
     * @param nickname 昵称
     * @return 任务列表
     */
    suspend fun updateNickName(nickname: String): HttpResp<Any> {
        return service.updateNickname(ProfileManager.userId, nickname)
    }

    /**
     * App检测升级
     *
     * @return 任务列表
     */
    suspend fun checkAppUpdate(versionName: String): HttpResp<AppUpdateResp> {
        return service.checkAppUpdate(versionName)
    }

    /**
     * 反馈接口
     *
     * @return 任务列表
     */
    suspend fun submitFeedback(description: String): HttpResp<Any> {
        return service.feedback(ProfileManager.userId, description)
    }

    /**
     * 更新头像接口
     *
     * @param file 头像文件
     * @return 任务列表
     */
    suspend fun updateAvatar(file: File): HttpResp<AvatarResp> {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("avatar", file.name, RequestBody.create(MediaType.parse("image/png"), file))
            .build()
        return service.updateAvatar(body)
    }
}