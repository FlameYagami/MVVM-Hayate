package com.mvvm.hayate.api

import com.mvvm.component.api.BaseApi
import com.mvvm.component.api.HttpFunc
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.model.app.AppUpdateResp
import com.mvvm.hayate.model.login.LoginReq
import com.mvvm.hayate.model.login.LoginResp
import io.reactivex.Observable
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
    fun login(http: LoginReq): Observable<LoginResp> {
        return service.login(http).map(HttpFunc())
    }

    /**
     * 修改昵称接口
     *
     * @param nickname 昵称
     * @return 任务列表
     */
    fun updateNickName(nickname: String): Observable<Any> {
        return service.updateNickname(ProfileManager.userId, nickname).map(HttpFunc())
    }

    /**
     * App检测升级
     *
     * @return 任务列表
     */
    fun checkAppUpdate(versionName: String): Observable<AppUpdateResp> {
        return service.checkAppUpdate(versionName).map(HttpFunc())
    }

    /**
     * 反馈接口
     *
     * @return 任务列表
     */
    fun submitFeedback(description: String): Observable<Any> {
        return service.feedback(ProfileManager.userId, description).map(HttpFunc())
    }

    /**
     * 更新头像接口
     *
     * @param file 头像文件
     * @return 任务列表
     */
    fun updateAvatar(file: File): Observable<Any> {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("avatar", file.name, RequestBody.create(MediaType.parse("image/png"), file))
            .build()
        return service.updateAvatar(body).map(HttpFunc())
    }
}