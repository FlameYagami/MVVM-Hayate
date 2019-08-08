package com.mvvm.component.utils

import java.net.URLEncoder

object StringUtils {

    fun encoded(paramString: String): String {
        if (paramString.isBlank()) return ""
        return try {
            URLEncoder.encode(String(paramString.toByteArray(), charset("UTF-8")), "UTF-8")
        } catch (e: Exception) {
            ""
        }
    }
}
