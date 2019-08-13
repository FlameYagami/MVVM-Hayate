package com.mvvm.component.utils

import android.content.Context
import android.content.SharedPreferences
import com.mvvm.component.BaseApplication.Companion.context

/**
 * SharedPreferences 轻量级存储工具
 */
class SpUtils {

    private var spPair = hashMapOf<String, SharedPreferences?>()

    companion object {

        const val DEFAULT_SP_NAME = "com.mvvm.hayate"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SpUtils()
        }

        private fun spInstance(spName: String = DEFAULT_SP_NAME): SharedPreferences {
            instance.spPair[spName] ?: apply {
                instance.spPair[spName] = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
            }
            return instance.spPair[spName] ?: throw Exception("An error occurred while creating sp named $spName")
        }

        /**
         * 存储一个String数据类型
         */
        fun putString(key: String, value: String, spName: String = DEFAULT_SP_NAME) {
            spInstance(spName).edit().putString(key, value).apply()
        }

        /**
         * 获取一个String数据类型
         */
        fun getString(key: String, defValue: String = "", spName: String = DEFAULT_SP_NAME): String {
            return spInstance(spName).getString(key, defValue) ?: ""
        }

        /**
         * 存储一个boolean数据类型
         */
        fun putBoolean(key: String, value: Boolean, spName: String = DEFAULT_SP_NAME) {
            spInstance(spName).edit().putBoolean(key, value).apply()
        }

        /**
         * 获取一个boolean数据类型
         */
        fun getBoolean(key: String, defValue: Boolean = false, spName: String = DEFAULT_SP_NAME): Boolean {
            return spInstance(spName).getBoolean(key, defValue)
        }

        /**
         * 删除数据
         */
        fun remove(key: String, spName: String = DEFAULT_SP_NAME) {
            spInstance(spName).edit().remove(key).apply()
        }
    }
}
