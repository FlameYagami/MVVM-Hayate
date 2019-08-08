package com.mvvm.component.uc.refresh

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
class CircleImageView : AppCompatImageView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    companion object {

        private const val KEY_SHADOW_COLOR = 0x1E000000
        private const val FILL_SHADOW_COLOR = 0x3D000000
        // PX
        private const val X_OFFSET = 0f
        private const val Y_OFFSET = 1.75f
        private const val SHADOW_RADIUS = 3.5f
        private const val SHADOW_ELEVATION = 4

        // Default background for the progress spinner
        const val CIRCLE_BG_LIGHT = -0x50506
        // Default offset in dips from the top of the view to where the progress spinner should stop
        const val DEFAULT_CIRCLE_TARGET = 64

        const val CIRCLE_DIAMETER = 40
        const val CIRCLE_DIAMETER_LARGE = 56
    }

    private var mListener: Animation.AnimationListener? = null
    private var mShadowRadius = 0

    init {
        val density = context.resources.displayMetrics.density
        val diameter = (CIRCLE_DIAMETER * density).toInt()
        val shadowYOffset = (density * Y_OFFSET).toInt()
        val shadowXOffset = (density * X_OFFSET).toInt()

        mShadowRadius = (density * SHADOW_RADIUS).toInt()

        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            val oval = OvalShadow(mShadowRadius, diameter)
            circle = ShapeDrawable(oval)
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.paint)
            circle.paint.setShadowLayer(
                    mShadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                    KEY_SHADOW_COLOR
            )
            val padding = mShadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
        }
        circle.paint.color = CIRCLE_BG_LIGHT
        background = circle
    }

    private fun elevationSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(measuredWidth + mShadowRadius * 2, measuredHeight + mShadowRadius * 2)
        }
    }

    fun setAnimationListener(listener: Animation.AnimationListener?) {
        mListener = listener
    }

    public override fun onAnimationStart() {
        super.onAnimationStart()
        mListener?.onAnimationStart(animation)
    }

    public override fun onAnimationEnd() {
        super.onAnimationEnd()
        mListener?.onAnimationEnd(animation)
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    fun setBackgroundColorRes(@ColorRes colorRes: Int) {
        setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }

    override fun setBackgroundColor(color: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = color
        }
    }

    private inner class OvalShadow(shadowRadius: Int, private val mCircleDiameter: Int) : OvalShape() {
        private val mRadialGradient: RadialGradient
        private val mShadowPaint: Paint = Paint()

        init {
            mShadowRadius = shadowRadius
            mRadialGradient = RadialGradient((mCircleDiameter / 2).toFloat(), (mCircleDiameter / 2).toFloat(),
                    mShadowRadius.toFloat(), intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
            mShadowPaint.shader = mRadialGradient
        }

        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@CircleImageView.width
            val viewHeight = this@CircleImageView.height
            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), (mCircleDiameter / 2 + mShadowRadius).toFloat(), mShadowPaint)
            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), (mCircleDiameter / 2).toFloat(), paint)
        }
    }
}
