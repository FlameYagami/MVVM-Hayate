package com.mvvm.component.uc.refresh

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.core.math.MathUtils
import androidx.core.view.*

class RefreshLayout constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs),
        NestedScrollingParent {

    companion object {

        // Maps to ProgressBar.Large style
        val LARGE = ProgressDrawable.LARGE
        // Maps to ProgressBar default style
        val DEFAULT = ProgressDrawable.DEFAULT

        private val LOG_TAG = RefreshLayout::class.java.simpleName
        private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)

        private const val MAX_ALPHA = 255
        private const val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()

        private const val DECELERATE_INTERPOLATION_FACTOR = 2f
        private const val INVALID_POINTER = -1
        private const val DRAG_RATE = .5f

        // Max amount of circle that can be filled by progress during swipe gesture,
        // where 1.0 is a full circle
        private const val MAX_PROGRESS_ANGLE = .8f
        private const val SCALE_DOWN_DURATION = 150
        private const val ALPHA_ANIMATION_DURATION = 300
        private const val ANIMATE_TO_TRIGGER_DURATION = 200
        private const val ANIMATE_TO_START_DURATION = 200

        // Default background for the progress spinner
        private const val CIRCLE_BG_LIGHT = -0x50506
        // Default offset in dips from the top of the view to where the progress spinner should stop
        private const val DEFAULT_CIRCLE_TARGET = 64

        private const val RETURN_TO_ORIGINAL_POSITION_TIMEOUT: Long = 300
        private const val ACCELERATE_INTERPOLATION_FACTOR = 1.5f
        private const val PROGRESS_BAR_HEIGHT = 4f
        private const val MAX_SWIPE_DISTANCE_FACTOR = .6f
        private const val REFRESH_TRIGGER_DISTANCE = 120
    }

    private var mTarget: View? = null // the target of the gesture
    private var mListener: OnRefreshListener? = null
    private var mCircleView: CircleImageView
    private var mProgress: ProgressDrawable
    private var mProgressBar: SwipeProgressBar

    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val mMediumAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)

    private var mInitialMotionY = 0f
    private var mInitialDownY = 0f
    private var mLastMotionY = 0f
    private var mActivePointerId = INVALID_POINTER

    private var isHeaderBeingDragged = false
    private var isFooterBeingDragged = false
    var isHeaderEnabled = true
    var isFooterEnabled = false

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private var mReturningToStart = false
    private val mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
    private val mAccelerateInterpolator = AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR)

    private var mCircleWidth = 0
    private var mCircleHeight = 0
    private var mCircleViewIndex = -1

    private var mHeaderAlphaStartAnimation: Animation? = null
    private var mHeaderAlphaMaxAnimation: Animation? = null
    private var mHeaderFrom = 0
    private var mHeaderOriginalOffsetTop = 0
    private var mHeaderCurrentTargetOffsetTop = 0
    private var mHeaderTotalDragDistance = 0f
    private var mHeaderStartingScale = 0f
    private var mSpinnerFinalOffset = 0f
    private var mHeaderNotify = false
    private var mHeaderScale = false
    private var mHeaderUsingCustomStart = false
    private var mHeaderRefreshing = false
    private var mHeaderOriginalOffsetCalculated = false

    private var mFooterFrom = 0
    //    private val mFooterOriginalOffsetTop = 0
    private var mFooterDistanceToTriggerSync = -1f
    private var mFooterFromPercentage = 0f
    private var mFooterCurrPercentage = 0f
    private var mFooterCurrentTargetOffsetTop = 0

    private var mProgressBarHeight = 0

    private val mNestedScrollingParentHelper: NestedScrollingParentHelper
    private val mNestedScrollingChildHelper: NestedScrollingChildHelper
    private var mNestedScrollInProgress = false
    private var mTotalUnconsumed = 0f
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)

    init {

        setWillNotDraw(false)

        val a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()

        val metrics = resources.displayMetrics
        mCircleWidth = (CircleImageView.CIRCLE_DIAMETER * metrics.density).toInt()
        mCircleHeight = (CircleImageView.CIRCLE_DIAMETER * metrics.density).toInt()

        mCircleView = CircleImageView(context)
        mProgress = ProgressDrawable(context, this)
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT)
        mCircleView.setImageDrawable(mProgress)
        mCircleView.visibility = View.GONE
        addView(mCircleView)

        isChildrenDrawingOrderEnabled = true
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffset = CircleImageView.DEFAULT_CIRCLE_TARGET * metrics.density
        mHeaderTotalDragDistance = mSpinnerFinalOffset

        mProgressBar = SwipeProgressBar(this)
        mProgressBarHeight = (metrics.density * PROGRESS_BAR_HEIGHT).toInt()

        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks(mCancel)
        removeCallbacks(mReturnToStartPosition)
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mReturnToStartPosition)
        removeCallbacks(mCancel)
    }

    private val mHeaderRefreshListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            if (mHeaderRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.alpha = MAX_ALPHA
                mProgress.start()
                if (mHeaderNotify) {
                    mListener?.onHeaderRefresh()
                }
            } else {
                mProgress.stop()
                mCircleView.visibility = View.GONE
                setColorViewAlpha(MAX_ALPHA)
                // Return the circle to its start position
                if (mHeaderScale) {
                    setAnimationProgress(0f)
                } else {
                    setHeaderTargetOffsetTopAndBottom(mHeaderOriginalOffsetTop - mHeaderCurrentTargetOffsetTop)
                }
            }
            mHeaderCurrentTargetOffsetTop = mCircleView.top
        }
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     */
    // Can't header and footer refresh both
    var isFooterRefreshing = false
        set(refreshing) {
            if (mHeaderRefreshing && refreshing) {
                return
            }

            if (isFooterRefreshing != refreshing) {
                ensureTarget()
                mFooterCurrPercentage = 0f
                field = refreshing
                if (isFooterRefreshing) {
                    mProgressBar.start()
                } else {
                    mProgressBar.stop()
                }
            }
        }

    private val mShrinkTrigger = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val percent = mFooterFromPercentage + (0 - mFooterFromPercentage) * interpolatedTime
            mProgressBar.setTriggerPercentage(percent)
        }
    }

    private val mReturnToStartPositionListener = object : BaseAnimationListener() {
        override fun onAnimationEnd(animation: Animation?) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            mFooterCurrentTargetOffsetTop = 0
        }
    }

    private val mShrinkAnimationListener = object : BaseAnimationListener() {
        override fun onAnimationEnd(animation: Animation?) {
            mFooterCurrPercentage = 0f
        }
    }

    private val mReturnToStartPosition = Runnable {
        mReturningToStart = true
//        animateFooterOffsetToStartPosition(
//            mFooterCurrentTargetOffsetTop + paddingTop,
//            mReturnToStartPositionListener
//        )
    }

    // Cancel the refresh gesture and animate everything back to its original state.
    private val mCancel = Runnable {
        mReturningToStart = true
        // Timeout fired since the user last moved their finger; animate the
        // trigger to 0 and put the target back at its original position
        mFooterFromPercentage = mFooterCurrPercentage
        mShrinkTrigger.duration = mMediumAnimationDuration.toLong()
        mShrinkTrigger.setAnimationListener(mShrinkAnimationListener)
        mShrinkTrigger.reset()
        mShrinkTrigger.interpolator = mDecelerateInterpolator
        startAnimation(mShrinkTrigger)
//        animateFooterOffsetToStartPosition(
//            mFooterCurrentTargetOffsetTop + paddingTop,
//            mReturnToStartPositionListener
//        )
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    val isRefreshing: Boolean
        get() = mHeaderRefreshing || isFooterRefreshing

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     */
    // Can't header and footer refresh both
    // scale and show
    /* requires update *//* notify */
    var isHeaderRefreshing: Boolean
        get() = mHeaderRefreshing
        set(refreshing) {
            if (isFooterRefreshing && refreshing) {
                return
            }

            if (refreshing && mHeaderRefreshing != refreshing) {
                mHeaderRefreshing = refreshing
                val endTarget = if (!mHeaderUsingCustomStart) {
                    (mSpinnerFinalOffset + mHeaderOriginalOffsetTop).toInt()
                } else {
                    mSpinnerFinalOffset.toInt()
                }
                setHeaderTargetOffsetTopAndBottom(
                        endTarget - mHeaderCurrentTargetOffsetTop
                )
                mHeaderNotify = false
                startScaleUpAnimation(mHeaderRefreshListener)
            } else {
                setHeaderRefreshing(refreshing, false)
            }
        }

    /**
     * Get the diameter of the progress circle that is displayed as part of the
     * swipe to refresh layout. This is not valid until a measure pass has
     * completed.
     *
     * @return Diameter in pixels of the progress circle view.
     */
    val progressCircleDiameter: Int
        get() = mCircleView.measuredHeight

    /**
     * @return `true` if child view almost scroll to bottom.
     */
    val isAlmostBottom: Boolean
        get() {
            return when (mTarget) {
                is AbsListView -> {
                    val absListView = mTarget as AbsListView
                    absListView.lastVisiblePosition >= absListView.count - 1
                }
                is ScrollingView -> {
                    val scrollingView = mTarget as ScrollingView
                    val offset = scrollingView.computeVerticalScrollOffset()
                    val range = scrollingView.computeVerticalScrollRange() - scrollingView.computeVerticalScrollExtent()
                    offset >= range
                }
                else -> !(mTarget?.canScrollVertically(1) ?: false)
            }
        }

    private val mAnimateHeaderToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int
            val endTarget = if (!mHeaderUsingCustomStart) {
                (mSpinnerFinalOffset - Math.abs(mHeaderOriginalOffsetTop)).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            targetTop = mHeaderFrom + ((endTarget - mHeaderFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView.top
            setHeaderTargetOffsetTopAndBottom(offset)
            mProgress.setArrowScale(1 - interpolatedTime)
        }
    }

    private val mAnimateHeaderToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveHeaderToStart(interpolatedTime)
        }
    }

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView.background.alpha = targetAlpha
        mProgress.alpha = targetAlpha
    }

    /**
     * The refresh indicator starting and resting position is always positioned
     * near the top of the refreshing content. This position is a consistent
     * location, but can be adjusted in either direction based on whether or not
     * there is a toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     * where the progress spinner is set to appear.
     * @param start The offset in pixels from the top of this view at which the
     * progress spinner should appear.
     * @param end   The offset in pixels from the top of this view at which the
     * progress spinner should come to rest after a successful swipe
     * gesture.
     */
    fun setProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        mHeaderScale = scale
        mCircleView.visibility = View.GONE
        mHeaderCurrentTargetOffsetTop = start
        mHeaderOriginalOffsetTop = mHeaderCurrentTargetOffsetTop
        mSpinnerFinalOffset = end.toFloat()
        mHeaderUsingCustomStart = true
        mCircleView.invalidate()
    }

    /**
     * The refresh indicator resting position is always positioned near the top
     * of the refreshing content. This position is a consistent location, but
     * can be adjusted in either direction based on whether or not there is a
     * toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     * where the progress spinner is set to appear.
     * @param end   The offset in pixels from the top of this view at which the
     * progress spinner should come to rest after a successful swipe
     * gesture.
     */
    fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        mSpinnerFinalOffset = end.toFloat()
        mHeaderScale = scale
        mCircleView.invalidate()
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    fun setSize(size: Int) {
        if (size != ProgressDrawable.LARGE && size != ProgressDrawable.DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        if (size == ProgressDrawable.LARGE) {
            mCircleWidth = (CircleImageView.CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        } else {
            mCircleWidth = (CircleImageView.CIRCLE_DIAMETER * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null)
        mProgress.updateSizes(size)
        mCircleView.setImageDrawable(mProgress)
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return when {
            mCircleViewIndex < 0 -> i
            i == childCount - 1 -> mCircleViewIndex // Draw the selected child last
            i >= mCircleViewIndex -> i + 1 // Move the children after the selected child earlier one
            else -> i // Keep the children before the selected child the same
        }
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    private fun setHeaderRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isFooterRefreshing && refreshing) {
            // Can't header and footer refresh both
            return
        }

        if (mHeaderRefreshing != refreshing) {
            mHeaderNotify = notify
            ensureTarget()
            mHeaderRefreshing = refreshing
            if (mHeaderRefreshing) {
                animateHeaderOffsetToCorrectPosition(mHeaderCurrentTargetOffsetTop, mHeaderRefreshListener)
            } else {
                startScaleDownAnimation(mHeaderRefreshListener)
            }
        }
    }

    private fun startScaleUpAnimation(listener: AnimationListener?) {
        mCircleView.visibility = View.VISIBLE
        // Pre API 11, alpha is used in place of scale up to show the
        // progress circle appearing.
        // Don't adjust the alpha during appearance otherwise.
        mProgress.alpha = MAX_ALPHA
        val mHeaderScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mHeaderScaleAnimation.duration = mMediumAnimationDuration.toLong()
        listener?.apply { mCircleView.setAnimationListener(this) }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mHeaderScaleAnimation)
    }

    /**
     * Pre API 11, this does an alpha animation.
     */
    private fun setAnimationProgress(progress: Float) {
        mCircleView.scaleX = progress
        mCircleView.scaleY = progress
    }

    private fun startScaleDownAnimation(listener: AnimationListener?) {
        val mHeaderScaleDownAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mHeaderScaleDownAnimation.duration = SCALE_DOWN_DURATION.toLong()
        mCircleView.setAnimationListener(listener)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mHeaderScaleDownAnimation)
    }

    private fun startHeaderProgressAlphaStartAnimation() {
        mHeaderAlphaStartAnimation = startHeaderAlphaAnimation(mProgress.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startHeaderProgressAlphaMaxAnimation() {
        mHeaderAlphaMaxAnimation = startHeaderAlphaAnimation(mProgress.alpha, MAX_ALPHA)
    }

    private fun startHeaderAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation? {
        // Pre API 11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (mHeaderScale) {
            return null
        }
        val alpha = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(alpha)
        return alpha
    }

    fun setHeaderTranslationY(translationY: Float) {
        mCircleView.translationY = translationY
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    fun setHeaderProgressBackgroundColorSchemeResource(colorRes: Int) {
        setHeaderProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, colorRes))
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color the background color
     */
    fun setHeaderProgressBackgroundColorSchemeColor(color: Int) {
        mCircleView.setBackgroundColor(color)
        mProgress.setBackgroundColor(color)
    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds the color resources
     */
    fun setHeaderColorSchemeResources(vararg colorResIds: Int) {
        val context = context
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i])
        }
        setHeaderColorSchemeColors(*colorRes)
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors the colors
     */
    fun setHeaderColorSchemeColors(vararg colors: Int) {
        ensureTarget()
        mProgress.setColorSchemeColors(*colors)
    }

    /**
     * Set the four colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     */
    fun setFooterColorSchemeResources(
            colorRes1: Int, colorRes2: Int, colorRes3: Int,
            colorRes4: Int
    ) {
        val context = context
        setFooterColorSchemeColors(
                ContextCompat.getColor(context, colorRes1),
                ContextCompat.getColor(context, colorRes2),
                ContextCompat.getColor(context, colorRes3),
                ContextCompat.getColor(context, colorRes4)
        )
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     */
    fun setFooterColorSchemeColors(color1: Int, color2: Int, color3: Int, color4: Int) {
        ensureTarget()
        mProgressBar.setColorScheme(color1, color2, color3, color4)
    }

    private fun ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        mTarget ?: apply {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mCircleView) {
                    mTarget = child
                    mHeaderOriginalOffsetTop = child.top + paddingTop
                    break
                }
            }
        }
        if (mFooterDistanceToTriggerSync == -1f) {
            if (parent != null && (parent as View).height > 0) {
                val metrics = resources.displayMetrics
                mFooterDistanceToTriggerSync = Math.min(
                        (parent as View).height * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density
                )
            }
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     */
    fun setDistanceToTriggerSync(distance: Int) {
        mHeaderTotalDragDistance = distance.toFloat()
    }

    private fun setTriggerPercentage(percent: Float) {
//        if (percent == 0f) {
//            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
//            // means we're trying to reset state, so there's nothing to reset in this case.
//            mFooterCurrPercentage = 0;
//            return;
//        }
        mFooterCurrPercentage = percent
        mProgressBar.setTriggerPercentage(percent)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        mProgressBar.draw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top
        if (childCount == 0) {
            return
        }
        mTarget ?: apply { ensureTarget() }
        mTarget ?: return
        val child = mTarget
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        child?.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        val circleWidth = mCircleView.measuredWidth
        val circleHeight = mCircleView.measuredHeight
        mCircleView.layout(
                width / 2 - circleWidth / 2, mHeaderCurrentTargetOffsetTop,
                width / 2 + circleWidth / 2, mHeaderCurrentTargetOffsetTop + circleHeight
        )
        mProgressBar.setBounds(0, height - mProgressBarHeight, width, height)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mTarget ?: apply { ensureTarget() }
        mTarget ?: return
        mTarget?.measure(
                View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY)
        )
        mCircleView.measure(
                View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY)
        )
        if (!mHeaderUsingCustomStart && !mHeaderOriginalOffsetCalculated) {
            mHeaderOriginalOffsetCalculated = true
            mHeaderOriginalOffsetTop = -mCircleView.measuredHeight
            mHeaderCurrentTargetOffsetTop = mHeaderOriginalOffsetTop
        }
        mCircleViewIndex = -1
        // Get the index of the circle view.
        for (index in 0 until childCount) {
            if (getChildAt(index) === mCircleView) {
                mCircleViewIndex = index
                break
            }
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    fun canChildScrollUp(): Boolean {
        return mTarget?.canScrollVertically(-1) ?: false
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     */
    fun canChildScrollDown(): Boolean {
        return if (mTarget is AbsListView) {
            val absListView = mTarget as AbsListView
            absListView.childCount > 0 && (absListView.lastVisiblePosition < absListView.count - 1 || absListView.getChildAt(0).bottom < absListView.width - absListView.paddingBottom)
        } else {
            mTarget?.canScrollVertically(1) ?: false
        }
    }

    private fun headerInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setHeaderTargetOffsetTopAndBottom(mHeaderOriginalOffsetTop - mCircleView.top)
                mActivePointerId = ev.getPointerId(0)
                isHeaderBeingDragged = false
                val initialDownY = getMotionEventY(ev, mActivePointerId)
                if (initialDownY == -1f) {
                    return false
                }
                mInitialDownY = initialDownY
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialDownY
                if (yDiff > mTouchSlop && !isHeaderBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop
                    isHeaderBeingDragged = true
                    mProgress.alpha = STARTING_PROGRESS_ALPHA
                }
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isHeaderBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }

        return isHeaderBeingDragged
    }

    private fun footerInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = ev.getPointerId(0)
                isFooterBeingDragged = false
                mFooterCurrPercentage = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y = ev.getY(pointerIndex)
                val yDiff = y - mInitialMotionY
                if (yDiff < -mTouchSlop) {
                    mLastMotionY = y
                    isFooterBeingDragged = true
                }
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isFooterBeingDragged = false
                mFooterCurrPercentage = 0f
                mActivePointerId = INVALID_POINTER
            }
        }
        return isFooterBeingDragged
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        return if (index < 0) {
            -1f
        } else ev.getY(index)
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        // Nope.
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    private fun headerTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                isHeaderBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y: Float
                y = try {
                    ev.getY(pointerIndex)
                } catch (e: IllegalArgumentException) {
                    mInitialMotionY
                }

                if (isHeaderBeingDragged) {
                    val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                    moveSpinner(overScrollTop)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                mActivePointerId = ev.getPointerId(index)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    }
                    return false
                }
                val pointerIndex = ev.findPointerIndex(mActivePointerId)
                val y = ev.getY(pointerIndex)
                isHeaderBeingDragged = false

                val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                finishSpinner(overScrollTop)
                mActivePointerId = INVALID_POINTER
                return false
            }
        }

        return isHeaderBeingDragged
    }

    private fun moveSpinner(overScrollTop: Float) {
        mProgress.showArrow(true)
        val originalDragPercent = overScrollTop / mHeaderTotalDragDistance
        if (originalDragPercent < 0) {
            return
        }
        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val adjustedPercent = Math.max(dragPercent - .4, 0.0).toFloat() * 5 / 3
        val extraOS = Math.abs(overScrollTop) - mHeaderTotalDragDistance
        val slingshotDist = if (mHeaderUsingCustomStart)
            mSpinnerFinalOffset - mHeaderOriginalOffsetTop
        else
            mSpinnerFinalOffset
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow((tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = mHeaderOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mCircleView.visibility != View.VISIBLE) {
            mCircleView.visibility = View.VISIBLE
        }
        if (!mHeaderScale) {
            mCircleView.scaleX = 1f
            mCircleView.scaleY = 1f
        }
        if (overScrollTop < mHeaderTotalDragDistance) {
            if (mHeaderScale) {
                setAnimationProgress(overScrollTop / mHeaderTotalDragDistance)
            }
            if (mProgress.alpha > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mHeaderAlphaStartAnimation)) {
                // Animate the alpha
                startHeaderProgressAlphaStartAnimation()
            }
            val strokeStart = adjustedPercent * .8f
            mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart))
            mProgress.setArrowScale(Math.min(1f, adjustedPercent))
        } else {
            if (mProgress.alpha < MAX_ALPHA && !isAnimationRunning(mHeaderAlphaMaxAnimation)) {
                // Animate the alpha
                startHeaderProgressAlphaMaxAnimation()
            }
        }
        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress.setProgressRotation(rotation)
        setHeaderTargetOffsetTopAndBottom(targetY - mHeaderCurrentTargetOffsetTop)
    }

    private fun finishSpinner(overScrollTop: Float) {
        if (overScrollTop > mHeaderTotalDragDistance) {
            setHeaderRefreshing(true, true)
        } else {
            // cancel refresh
            mHeaderRefreshing = false
            mProgress.setStartEndTrim(0f, 0f)
            var listener: AnimationListener? = null
            if (!mHeaderScale) {
                listener = object : AnimationListener {

                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        if (!mHeaderScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}

                }
            }
            animateHeaderOffsetToStartPosition(mHeaderCurrentTargetOffsetTop, listener)
            mProgress.showArrow(false)
        }
    }

    private fun footerTouchEvent(ev: MotionEvent) {
        val action = ev.actionMasked

        val pointerIndex: Int
        val y: Float
        val yDiff: Float
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = ev.getPointerId(0)
                isFooterBeingDragged = false
                mFooterCurrPercentage = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return
                }

                y = ev.getY(pointerIndex)
                yDiff = y - mInitialMotionY

                if (!isFooterBeingDragged && yDiff < -mTouchSlop) {
                    isFooterBeingDragged = true
                }

                if (isFooterBeingDragged) {
                    val input = MathUtils.clamp(-yDiff, 0f, mFooterDistanceToTriggerSync)
                    setTriggerPercentage(mAccelerateInterpolator.getInterpolation(input / mFooterDistanceToTriggerSync))
                    mLastMotionY = y
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                mLastMotionY = ev.getY(index)
                mActivePointerId = ev.getPointerId(index)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (mActivePointerId == INVALID_POINTER && pointerIndex < 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    }
                    return
                }

                y = try {
                    ev.getY(pointerIndex)
                } catch (e: Throwable) {
                    0f
                }

                yDiff = y - mInitialMotionY

                if (action == MotionEvent.ACTION_UP && -yDiff > mFooterDistanceToTriggerSync) {
                    startFooterRefresh()
                } else {
                    mCancel.run()
                }

                isFooterBeingDragged = false
                mFooterCurrPercentage = 0f
                mActivePointerId = INVALID_POINTER
            }
        }
    }

    private fun startFooterRefresh() {
        removeCallbacks(mCancel)
        mReturnToStartPosition.run()
        isFooterRefreshing = true
        mNestedScrollInProgress = false
        mListener?.onFooterRefresh()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        val action = ev.actionMasked
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) mReturningToStart = false

        var mIsBeingDragged = false
        if (isEnabled && !mReturningToStart && !mHeaderRefreshing && !isFooterRefreshing) {
            if (!isFooterBeingDragged && isHeaderEnabled && !canChildScrollUp()) mIsBeingDragged = headerInterceptTouchEvent(ev)
            if (!isHeaderBeingDragged && isFooterEnabled && !canChildScrollDown()) mIsBeingDragged = mIsBeingDragged or footerInterceptTouchEvent(ev)
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) mReturningToStart = false
        if (isEnabled && !mReturningToStart && !mHeaderRefreshing && !isFooterRefreshing) {
            if (!isFooterBeingDragged && isHeaderEnabled && !canChildScrollUp()) headerTouchEvent(ev)
            if (!isHeaderBeingDragged && isFooterEnabled && !canChildScrollDown()) footerTouchEvent(ev)
        }
        return true
    }

    private fun animateHeaderOffsetToCorrectPosition(from: Int, listener: AnimationListener?) {
        mHeaderFrom = from
        mAnimateHeaderToCorrectPosition.reset()
        mAnimateHeaderToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateHeaderToCorrectPosition.interpolator = mDecelerateInterpolator
        listener?.apply { mCircleView.setAnimationListener(this) }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mAnimateHeaderToCorrectPosition)
    }

    private fun animateHeaderOffsetToStartPosition(from: Int, listener: AnimationListener?) {
        if (mHeaderScale) {
            // Scale the item back down
            startHeaderScaleDownReturnToStartAnimation(from, listener)
        } else {
            mHeaderFrom = from
            mAnimateHeaderToStartPosition.reset()
            mAnimateHeaderToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateHeaderToStartPosition.interpolator = mDecelerateInterpolator
            listener?.apply { mCircleView.setAnimationListener(this) }
            mCircleView.clearAnimation()
            mCircleView.startAnimation(mAnimateHeaderToStartPosition)
        }
    }

    private fun moveHeaderToStart(interpolatedTime: Float) {
        val targetTop = mHeaderFrom + ((mHeaderOriginalOffsetTop - mHeaderFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView.top
        setHeaderTargetOffsetTopAndBottom(offset /* requires update */)
    }

    private fun startHeaderScaleDownReturnToStartAnimation(from: Int, listener: AnimationListener?) {
        mHeaderFrom = from
        mHeaderStartingScale = mCircleView.scaleX
        val mHeaderScaleDownToStartAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val targetScale = mHeaderStartingScale + -mHeaderStartingScale * interpolatedTime
                setAnimationProgress(targetScale)
                moveHeaderToStart(interpolatedTime)
            }
        }
        mHeaderScaleDownToStartAnimation.duration = SCALE_DOWN_DURATION.toLong()
        listener?.apply { mCircleView.setAnimationListener(this) }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mHeaderScaleDownToStartAnimation)
    }

    private fun setHeaderTargetOffsetTopAndBottom(offset: Int) {
        mCircleView.bringToFront()
        mCircleView.offsetTopAndBottom(offset)
        mHeaderCurrentTargetOffsetTop = mCircleView.top
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionY = ev.getY(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    interface OnRefreshListener {
        fun onHeaderRefresh()

        fun onFooterRefresh()
    }

    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private open inner class BaseAnimationListener : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {}

        override fun onAnimationRepeat(animation: Animation?) {}
    }

    private fun updatePositionTimeout() {
        removeCallbacks(mCancel)
        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT)
    }

    // NestedScrollingParent

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return (isEnabled && !mReturningToStart && !isRefreshing && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed.toInt()
                mTotalUnconsumed = 0f
            } else {
                mTotalUnconsumed -= dy.toFloat()
                consumed[1] = dy
            }
            moveSpinner(mTotalUnconsumed)
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mHeaderUsingCustomStart && dy > 0 && mTotalUnconsumed == 0f && Math.abs(dy - consumed[1]) > 0) {
            mCircleView.visibility = View.GONE
        }

        // Now let our nested parent consume the leftovers
        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed)
            mTotalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow)

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy).toFloat()
            moveSpinner(mTotalUnconsumed)
        }
    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }


//    private fun animateFooterOffsetToStartPosition(from: Int, listener: AnimationListener) {
//        mFooterFrom = from
//        mAnimateFooterToStartPosition.reset()
//        mAnimateFooterToStartPosition.duration = mMediumAnimationDuration.toLong()
//        mAnimateFooterToStartPosition.setAnimationListener(listener)
//        mAnimateFooterToStartPosition.interpolator = mDecelerateInterpolator
//        mTarget?.startAnimation(mAnimateFooterToStartPosition)
//    }

//    private val mAnimateFooterToStartPosition = object : Animation() {
//        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
//            var targetTop = 0
//            if (mFooterFrom != mFooterOriginalOffsetTop) {
//                targetTop = mFooterFrom + ((mFooterOriginalOffsetTop - mFooterFrom) * interpolatedTime).toInt()
//            }
//            var offset = targetTop - mTarget!!.top
//            val currentTop = mTarget!!.top
//            if (offset + currentTop > 0) {
//                offset = 0 - currentTop
//            }
//            setFooterTargetOffsetTopAndBottom(offset);
//        }
//    }

//    private void setFooterTargetOffsetTopAndBottom(int offset)
//    {
//        mTarget.offsetTopAndBottom(offset);
//        mFooterCurrentTargetOffsetTop = mTarget.getTop();
//    }

//    private void updateContentOffsetTop(int targetTop)
//    {
//        final int currentTop = mTarget.getTop();
//        if (targetTop < -mFooterDistanceToTriggerSync) {
//            targetTop = -(int) mFooterDistanceToTriggerSync;
//        } else if (targetTop > 0) {
//            targetTop = 0;
//        }
//        setFooterTargetOffsetTopAndBottom(targetTop - currentTop);
//    }
}
