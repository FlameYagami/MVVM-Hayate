package com.mvvm.hayate.api

import com.mvvm.component.api.HttpResp
import com.mvvm.hayate.model.app.AppUpdateResp
import com.mvvm.hayate.model.login.LoginReq
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.model.profile.AvatarResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }

    @POST("login")
    suspend fun login(@Body Body: LoginReq): HttpResp<LoginResp>

    @FormUrlEncoded
    @POST("nickname")
    suspend fun updateNickname(
        @Field("userId") userId: String,
        @Field("nickname") nickname: String
    ): HttpResp<Any>

    @POST("avatar")
    suspend fun updateAvatar(@Body Body: RequestBody): HttpResp<AvatarResp>

    @FormUrlEncoded
    @POST("feedback")
    suspend fun feedback(
        @Field("userId") userId: String,
        @Field("description") description: String
    ): HttpResp<Any>

    @FormUrlEncoded
    @POST("appUpdate")
    suspend fun checkAppUpdate(
        @Field("versionName") versionName: String
    ): HttpResp<AppUpdateResp>
}
