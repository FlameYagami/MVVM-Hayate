package com.mvvm.component.api

import com.google.gson.JsonParseException
import com.mvvm.component.BaseApplication.Companion.context
import com.mvvm.component.R
import com.orhanobut.logger.Logger
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLHandshakeException

class ApiThrowable(val resultCode: String, val resultMessage: String) : Throwable()

fun applyThrowableTransform(throwable: Throwable): String {
    Logger.e("applyThrowableTransform => ${throwable.message}")
    return when (throwable) {
        is ApiThrowable -> throwable.resultMessage
        is HttpException -> context.getString(R.string.network_exception)
        is SocketTimeoutException -> context.getString(R.string.connection_timeout)
        is SSLHandshakeException -> context.getString(R.string.handshake_exception)
        is JSONException, is JsonParseException, is ParseException -> context.getString(R.string.json_exception)
        is UnknownHostException, is ConnectException, is SocketException -> context.getString(R.string.connect_server_exception)
        else -> context.getString(R.string.unknown_exception)
    }
}