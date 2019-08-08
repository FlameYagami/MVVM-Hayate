package com.mvvm.component.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.mvvm.component.R

/**
 * Created by FlameYagami on 2018/4/24.
 */

object ColorUtils {

    /**
     * Color的rgb字符串转Color的Int字符串
     * rgb -- "255255255"
     * return colorInt -- "-12590395"
     */
    fun rgb2Int(rgb: String): Int {
        return Color.rgb(rgb.substring(0, 3).toInt(), rgb.substring(3, 6).toInt(), rgb.substring(6, 9).toInt())
    }

    /**
     * Color的Int转Color的rgb字符串
     * color - -12590395
     * return rgb -- "255255255"
     */
    fun int2Rgb(color: Int): String {
        //eg:-65523 转换为 十进制
        val red = String.format("%03d", color and 0xff0000 shr 16)
        val green = String.format("%03d", color and 0x00ff00 shr 8)
        val blue = String.format("%03d", color and 0x0000ff)
        return "$red$green$blue"
    }

    /**
     * Color的16进制颜色值 转 Color的Int整型
     * colorHex - Color的16进制颜色值——#3FE2C5
     * return colorInt - -12590395
     */
    fun hex2Int(colorHex: String): Int {
        return Color.parseColor(colorHex)
    }

    fun getColor(standardColor: Int, position: Float): String {
        return when {
            position < 0.0F -> 0.0F
            position > 1.0F -> 1.0F
            else -> position
        }.let {
            val hsvColors = FloatArray(3)
            Color.colorToHSV(standardColor, hsvColors)
            val colorInt = if (it <= 0.5F) {
                // Position由[0.0,0.5]转换V至[0.2,1.0]
                val v = position / 0.625F + 0.2F
                Color.HSVToColor(floatArrayOf(hsvColors[0], 1F, v))
            } else {
                // Position由[0.5,1.0]转换V至[1.0,0.2]
                val s = 1 - (position - 0.5F) * 1.6F
                Color.HSVToColor(floatArrayOf(hsvColors[0], s, 1F))
            }
            int2Rgb(colorInt)
        }
    }

    fun getAlphaPosition(colorRgba: String): Float {
        return colorRgba.takeIf {
            12 == it.length
        }?.substring(9, 12)?.toIntOrNull()?.let {
            it / 255f
        } ?: 0.5F
    }

    fun getStandardPair(colorRgba: String?): Pair<Int, Float>? {
        return colorRgba?.takeIf {
            12 == it.length || 9 == it.length
        }?.substring(0, 9)?.let {
            getStandardPair(ColorUtils.rgb2Int(it))
        }
    }

    fun getStandardPair(@ColorInt color: Int): Pair<Int, Float> {
        val hsvColors = FloatArray(3)
        Color.colorToHSV(color, hsvColors)
        val colorInt = Color.HSVToColor(floatArrayOf(hsvColors[0], 1f, 1f))
        val s = hsvColors[1]
        val v = hsvColors[2]
        val position = if (1.0F == s) {
            // V由[0.2,1.0]转换Position至[0.0,0.5]
            val position = (v - 0.2F) * 0.625F
            when {
                position <= 0.0F -> 0.0F
                position >= 0.5F -> 0.5F
                else -> position
            }
        } else {
            // S由[1.0,0.2]转换Position至[0.5,1.0]
            val position = (1 - s) / 1.6F + 0.5F
            when {
                position <= 0.5F -> 0.5F
                position >= 1.0F -> 1.0F
                else -> position
            }
        }
        return colorInt to position
    }

    fun getGradientDrawable(color: Int, shape: Int, context: Context): GradientDrawable {
        return GradientDrawable().apply {
            setShape(shape)
            setColor(color)
            setStroke(DisplayUtils.dp2px(1), ContextCompat.getColor(context, R.color.colorGrey))
            cornerRadius = DisplayUtils.dp2px(4).toFloat()
        }
    }
}
