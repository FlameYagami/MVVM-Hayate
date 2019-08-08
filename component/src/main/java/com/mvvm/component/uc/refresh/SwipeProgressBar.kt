package com.mvvm.component.uc.refresh

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Custom progress bar that shows a cycle of colors as widening circles that
 * overdraw each other. When finished, the bar is cleared from the inside out as
 * the main cycle continues. Before running, this can also indicate how close
 * the user is to triggering something (e.g. how far they need to pull down to
 * trigger a refresh).
 */
internal class SwipeProgressBar(private val mParent: View) {

    companion object {

        private val SUPPORT_CLIP_RECT_DIFFERENCE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2

        // Default progress animation colors are grays.
        private const val COLOR1 = -0x4d000000
        private const val COLOR2 = -0x80000000
        private const val COLOR3 = 0x4d000000
        private const val COLOR4 = 0x1a000000

        // The duration of the animation cycle.
        private const val ANIMATION_DURATION_MS = 2000

        // The duration of the animation to clear the bar.
        private const val FINISH_ANIMATION_DURATION_MS = 1000

        // Interpolator for varying the speed of the animation.
        private val INTERPOLATOR = FastOutSlowInInterpolator()
    }

    private val mPaint = Paint()
    private var mTriggerPercentage: Float = 0.toFloat()
    private var mStartTime: Long = 0
    private var mFinishTime: Long = 0
    private var mRunning: Boolean = false

    // Colors used when rendering the animation,
    private var mColor1: Int = 0
    private var mColor2: Int = 0
    private var mColor3: Int = 0
    private var mColor4: Int = 0

    private val mBounds = Rect()

    /**
     * @return Return whether the progress animation is currently running.
     */
    val isRunning: Boolean
        get() = mRunning || mFinishTime > 0

    init {
        mColor1 = COLOR1
        mColor2 = COLOR2
        mColor3 = COLOR3
        mColor4 = COLOR4
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     *
     * @param color1 Integer representation of a color.
     * @param color2 Integer representation of a color.
     * @param color3 Integer representation of a color.
     * @param color4 Integer representation of a color.
     */
    fun setColorScheme(color1: Int, color2: Int, color3: Int, color4: Int) {
        mColor1 = color1
        mColor2 = color2
        mColor3 = color3
        mColor4 = color4
    }

    /**
     * Update the progress the user has made toward triggering the swipe
     * gesture. and use this value to update the percentage of the trigger that
     * is shown.
     */
    fun setTriggerPercentage(triggerPercentage: Float) {
        mTriggerPercentage = triggerPercentage
        mStartTime = 0
        ViewCompat.postInvalidateOnAnimation(
            mParent, mBounds.left, mBounds.top, mBounds.right, mBounds.bottom
        )
    }

    /**
     * Start showing the progress animation.
     */
    fun start() {
        if (!mRunning) {
            mTriggerPercentage = 0f
            mStartTime = AnimationUtils.currentAnimationTimeMillis()
            mRunning = true
            mParent.postInvalidate()
        }
    }

    /**
     * Stop showing the progress animation.
     */
    fun stop() {
        if (mRunning) {
            mTriggerPercentage = 0f
            mFinishTime = AnimationUtils.currentAnimationTimeMillis()
            mRunning = false
            mParent.postInvalidate()
        }
    }

    fun draw(canvas: Canvas) {
        // API < 18 do not support clipRect(Region.Op.DIFFERENCE).
        // So draw twice for finish animation
        if (draw(canvas, true)) {
            draw(canvas, false)
        }
    }

    private fun draw(canvas: Canvas, first: Boolean): Boolean {
        val bounds = mBounds
        val width = bounds.width()
        val cx = bounds.centerX()
        val cy = bounds.centerY()
        var drawTriggerWhileFinishing = false
        var drawAgain = false
        var restoreCount = canvas.save()
        canvas.clipRect(bounds)

        if (mRunning || mFinishTime > 0) {
            val now = AnimationUtils.currentAnimationTimeMillis()
            val elapsed = (now - mStartTime) % ANIMATION_DURATION_MS
            val iterations = (now - mStartTime) / ANIMATION_DURATION_MS
            val rawProgress = elapsed / (ANIMATION_DURATION_MS / 100f)

            // If we're not running anymore, that means we're running through
            // the finish animation.
            if (!mRunning) {
                // If the finish animation is done, don't draw anything, and
                // don't re-post.
                if (now - mFinishTime >= FINISH_ANIMATION_DURATION_MS) {
                    mFinishTime = 0
                    return false
                }

                // Otherwise, use a 0 opacity alpha layer to clear the animation
                // from the inside out. This layer will prevent the circles from
                // drawing within its bounds.
                val finishElapsed = (now - mFinishTime) % FINISH_ANIMATION_DURATION_MS
                val finishProgress = finishElapsed / (FINISH_ANIMATION_DURATION_MS / 100f)
                val pct = finishProgress / 100f
                // Radius of the circle is half of the screen.
                val clearRadius = width / 2 * INTERPOLATOR.getInterpolation(pct)
                if (SUPPORT_CLIP_RECT_DIFFERENCE) {
                    canvas.save()
                    canvas.clipRect(cx - clearRadius, bounds.top.toFloat(), cx + clearRadius, bounds.bottom.toFloat())
                    canvas.restore()
//                    mClipRect.set(cx - clearRadius, bounds.top.toFloat(), cx + clearRadius, bounds.bottom.toFloat())
//                    canvas.clipRect(mClipRect, Region.Op.DIFFERENCE)
                } else {
                    canvas.save()
                    if (first) {
                        // First time left
                        drawAgain = true
                        canvas.clipRect(bounds.left.toFloat(), bounds.top.toFloat(), cx - clearRadius, bounds.bottom.toFloat())
//                        mClipRect.set(bounds.left.toFloat(), bounds.top.toFloat(), cx - clearRadius, bounds.bottom.toFloat())
                    } else {
                        // Second time right
                        canvas.clipRect(cx + clearRadius, bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
//                        mClipRect.set(cx + clearRadius, bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
                    }
                    canvas.restore()
//                    canvas.clipRect(mClipRect)
                }
                // Only draw the trigger if there is a space in the center of
                // this refreshing view that needs to be filled in by the
                // trigger. If the progress view is just still animating, let it
                // continue animating.
                drawTriggerWhileFinishing = true
            }

            // First fill in with the last color that would have finished drawing.
            if (iterations == 0L) {
                canvas.drawColor(mColor1)
            } else {
                if (rawProgress >= 0 && rawProgress < 25) {
                    canvas.drawColor(mColor4)
                } else if (rawProgress >= 25 && rawProgress < 50) {
                    canvas.drawColor(mColor1)
                } else if (rawProgress >= 50 && rawProgress < 75) {
                    canvas.drawColor(mColor2)
                } else {
                    canvas.drawColor(mColor3)
                }
            }

            // Then draw up to 4 overlapping concentric circles of varying radii, based on how far
            // along we are in the cycle.
            // progress 0-50 draw mColor2
            // progress 25-75 draw mColor3
            // progress 50-100 draw mColor4
            // progress 75 (wrap to 25) draw mColor1
            if (rawProgress in 0.0..25.0) {
                val pct = (rawProgress + 25) * 2 / 100f
                drawCircle(canvas, cx.toFloat(), cy.toFloat(), mColor1, pct)
            }
            if (rawProgress in 0.0..50.0) {
                val pct = rawProgress * 2 / 100f
                drawCircle(canvas, cx.toFloat(), cy.toFloat(), mColor2, pct)
            }
            if (rawProgress in 25.0..75.0) {
                val pct = (rawProgress - 25) * 2 / 100f
                drawCircle(canvas, cx.toFloat(), cy.toFloat(), mColor3, pct)
            }
            if (rawProgress in 50.0..100.0) {
                val pct = (rawProgress - 50) * 2 / 100f
                drawCircle(canvas, cx.toFloat(), cy.toFloat(), mColor4, pct)
            }
            if (rawProgress in 75.0..100.0) {
                val pct = (rawProgress - 75) * 2 / 100f
                drawCircle(canvas, cx.toFloat(), cy.toFloat(), mColor1, pct)
            }
            if (mTriggerPercentage > 0 && drawTriggerWhileFinishing) {
                // There is some portion of trigger to draw. Restore the canvas,
                // then draw the trigger. Otherwise, the trigger does not appear
                // until after the bar has finished animating and appears to
                // just jump in at a larger width than expected.
                canvas.restoreToCount(restoreCount)
                restoreCount = canvas.save()
                canvas.clipRect(bounds)
                drawTrigger(canvas, cx, cy)
            }
            // Keep running until we finish out the last cycle.
            ViewCompat.postInvalidateOnAnimation(
                mParent, bounds.left, bounds.top, bounds.right, bounds.bottom
            )
        } else {
            // Otherwise if we're in the middle of a trigger, draw that.
            if (mTriggerPercentage > 0 && mTriggerPercentage <= 1.0) {
                drawTrigger(canvas, cx, cy)
            }
        }
        canvas.restoreToCount(restoreCount)
        return drawAgain
    }

    private fun drawTrigger(canvas: Canvas, cx: Int, cy: Int) {
        mPaint.color = mColor1
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), cx * mTriggerPercentage, mPaint)
    }

    /**
     * Draws a circle centered in the view.
     *
     * @param canvas the canvas to draw on
     * @param cx the center x coordinate
     * @param cy the center y coordinate
     * @param color the color to draw
     * @param pct the percentage of the view that the circle should cover
     */
    private fun drawCircle(canvas: Canvas, cx: Float, cy: Float, color: Int, pct: Float) {
        mPaint.color = color
        canvas.save()
        canvas.translate(cx, cy)
        val radiusScale = INTERPOLATOR.getInterpolation(pct)
        canvas.scale(radiusScale, radiusScale)
        canvas.drawCircle(0f, 0f, cx, mPaint)
        canvas.restore()
    }

    /**
     * Set the drawing bounds of this SwipeProgressBar.
     */
    fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        mBounds.left = left
        mBounds.top = top
        mBounds.right = right
        mBounds.bottom = bottom
    }
}
