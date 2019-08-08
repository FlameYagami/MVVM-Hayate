package com.mvvm.component.utils

import android.content.Context
import android.content.SharedPreferences
import com.mvvm.component.BaseApplication.Companion.context

/**
 * SharedPreferences 轻量级存储工具
 */
class SpUtils {

    private var spDefault: SharedPreferences? = null
    private var spSecond: SharedPreferences? = null
    private var spThird: SharedPreferences? = null

    private var spPair = hashMapOf(
            Pair(SpType.DEFAULT, spDefault),
            Pair(SpType.SECOND, spSecond),
            Pair(SpType.THIRD, spThird)
    )

    private fun spInstance(spType: SpType = SpType.DEFAULT): SharedPreferences {
        instance.spPair[spType] ?: apply {
            instance.spPair[spType] = context.getSharedPreferences(spType.value(), Context.MODE_PRIVATE)
        }
        return instance.spPair[spType] ?: throw Exception("An error occurred while creating sp named ${spType.value()}")
    }

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SpUtils()
        }

        /**
         * 存储一个String数据类型
         */
        fun putString(key: String, value: String, spType: SpType = SpType.DEFAULT) {
            instance.spInstance(spType).edit().putString(key, value).apply()
        }

        /**
         * 获取一个String数据类型
         */
        fun getString(key: String, defValue: String = "", spType: SpType = SpType.DEFAULT): String {
            return instance.spInstance(spType).getString(key, defValue) ?: ""
        }

        /**
         * 存储一个boolean数据类型
         */
        fun putBoolean(key: String, value: Boolean, spType: SpType = SpType.DEFAULT) {
            instance.spInstance(spType).edit().putString(key, value.toString()).apply()
        }

        /**
         * 获取一个boolean数据类型
         */
        fun getBoolean(key: String, defValue: String = "", spType: SpType = SpType.DEFAULT): Boolean {
            return instance.spInstance(spType).getString(key, defValue) == true.toString()
        }

        /**
         * 删除数据
         */
        fun remove(key: String, spType: SpType = SpType.DEFAULT) {
            instance.spInstance(spType).edit().remove(key).apply()
        }
    }
}

enum class SpType(private val value: String) {

    DEFAULT("default"), SECOND("second"), THIRD("third");

    fun value(): String {
        return this.value
    }
}

