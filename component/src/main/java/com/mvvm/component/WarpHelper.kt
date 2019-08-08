package com.mvvm.component

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.mvvm.component.ext.internalStartActivity
import java.io.Serializable

/**
 * 折越辅助类
 */
class WarpPair(
    val clazz: Class<out Activity>,
    vararg val params: Pair<String, Any?>
) : Serializable

inline fun <reified T : Activity> warpStandBy(vararg params: Pair<String, Any?>): WarpPair =
    WarpPair(T::class.java, *params)

fun Context.startActivity(warpPair: WarpPair) = internalStartActivity(this, warpPair.clazz, warpPair.params)

/**
 * 折越辅助类
 */
class WarpIntent(
    val clazz: Class<out Activity>,
    val intent: Intent
) : Serializable

inline fun <reified T : Activity> warpStandBy(intent: Intent): WarpIntent = WarpIntent(T::class.java, intent)

fun Context.startActivity(warpIntent: WarpIntent) {
    // 如果clazz为Activity类型则代表系统级跳转、否则构建跳转ComponentName
    if (warpIntent.clazz == Activity::class.java) {
        startActivity(warpIntent.intent)
    } else {
        startActivity(warpIntent.intent.also { it.component = ComponentName(this, warpIntent.clazz) })
    }
}
