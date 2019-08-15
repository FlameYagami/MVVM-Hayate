package com.mvvm.hayate.api

import com.mvvm.component.api.HttpResp
import com.mvvm.hayate.model.app.AppUpdateResp
import com.mvvm.hayate.model.login.LoginReq
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.model.profile.AvatarResp
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {

    companion object {
        const val BASE_URL = "https://www.github.com"
    }

    @POST("login")
    fun login(@Body Body: LoginReq): Observable<HttpResp<LoginResp>>

    @FormUrlEncoded
    @POST("nickname")
    fun updateNickname(
        @Field("userId") userId: String,
        @Field("nickname") nickname: String
    ): Observable<HttpResp<LoginResp>>

    @FormUrlEncoded
    @POST("appUpdate")
    fun checkAppUpdate(
        @Field("versionName") versionName: String
    ): Observable<HttpResp<AppUpdateResp>>

    @FormUrlEncoded
    @POST("feedback")
    fun feedback(
        @Field("userId") userId: String,
        @Field("description") description: String
    ): Observable<HttpResp<Any>>

    @POST("avatar")
    fun updateAvatar(@Body Body: RequestBody): Observable<HttpResp<AvatarResp>>
}
