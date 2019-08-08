package com.mvvm.component.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import java.lang.reflect.Type
import java.util.*

/**
 * Created by FlameYagami on 2016/7/12.
 */
object JsonUtils {

    /**
     * 序列化
     *
     * @param obj 对象
     * @return Json
     */
    fun serializer(obj: Any): String {
        return GsonBuilder().create().toJson(obj)
    }

    /**
     * 反序列化对象
     *
     * @param jsonData Json
     * @return 对象
     */
    @Throws(JsonSyntaxException::class)
    inline fun <reified T> deserializer(jsonData: String): T? {
        return try {
            Gson().fromJson(jsonData, T::class.java)
        } catch (e: Exception) {
            Logger.e("Deserializer Error => ${e.message}")
            null
        }
    }

    /**
     * 反序列化对象
     *
     * @param jsonData Json
     * @return 对象
     */
    @Throws(JsonSyntaxException::class)
    fun <T> deserializer(jsonData: String, type: Type): T? {
        return Gson().fromJson<T>(jsonData, type)
    }

    /**
     * 反序列化数组对象
     *
     * @param jsonData Json
     * @return 数组集合
     */
    inline fun <reified T> deserializerArray(jsonData: String): List<T>? {
        return try {
            val array = Gson().fromJson(jsonData, Array<T>::class.java)
            listOf(*array)
        } catch (e: Exception) {
            Logger.e("Deserializer Error => ${e.message}")
            null
        }
    }

    /**
     * 反序列化集合对象
     *
     * @param jsonData Json
     * @return 对象集合
     */
    inline fun <reified T> deserializerList(jsonData: String): List<T>? {
        val arrayList = ArrayList<T>()
        try {
            val type = object : TypeToken<ArrayList<JsonObject>>() {

            }.type
            val jsonObjects = Gson().fromJson<List<JsonObject>>(jsonData, type)
            for (jsonObject in jsonObjects) {
                arrayList.add(Gson().fromJson(jsonObject, T::class.java))
            }
        } catch (e: Exception) {
            Logger.e("Deserializer Error => ${e.message}")
            return null
        }
        return arrayList
    }
}