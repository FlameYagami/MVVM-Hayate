package com.mvvm.component.ext

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun View.visible() {
	if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.gone() {
	if (visibility != View.GONE) visibility = View.GONE
}

fun View.inVisible() {
	if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

val View.isVisible: Boolean
	get() = visibility == View.VISIBLE

val View.isGone: Boolean
	get() = visibility == View.GONE

val View.isInVisible: Boolean
	get() = visibility == View.INVISIBLE

inline fun <reified T : View> View.find(@IdRes id: Int): T = findViewById(id)

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String = resources.getString(stringResId)

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
	val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	this.requestFocus()
	imm.showSoftInput(this, 0)
}

/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
	try {
		val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
	} catch (ignored: RuntimeException) {
	}
	return false
}

/**
 * Extension method to simplify view binding.
 */
fun <T : ViewDataBinding> View.bind() = DataBindingUtil.bind<T>(this) as T

/**
 * Extension method to set View's height.
 */
fun View.setHeight(value: Int) {
	val lp = layoutParams
	lp?.let {
		lp.height = value
		layoutParams = lp
	}
}

/**
 * Extension method to set View's width.
 */
fun View.setWidth(value: Int) {
	val lp = layoutParams
	lp?.let {
		lp.width = value
		layoutParams = lp
	}
}

/**
 * Extension method to remove the required boilerplate for running code after a view has been
 * inflated and measured.
 *
 * @author Antonio Leiva
 * @see <a href="https://antonioleiva.com/kotlin-ongloballayoutlistener/>Kotlin recipes: OnGlobalLayoutListener</a>
 */
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
	viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			if (measuredWidth > 0 && measuredHeight > 0) {
				viewTreeObserver.removeOnGlobalLayoutListener(this)
				f()
			}
		}
	})
}

/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(context.getColorCompat(R.color.colorAccent), { })
 */
fun View.doOnLayout(onLayout: (View) -> Boolean) {
	addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
		override fun onLayoutChange(
			view: View, left: Int, top: Int, right: Int, bottom: Int,
			oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
		) {
			if (onLayout(view)) {
				view.removeOnLayoutChangeListener(this)
			}
		}
	})
}

/**
 * Extension method to resize View with height & width.
 */
fun View.resize(width: Int, height: Int) {
	val lp = layoutParams
	lp?.let {
		lp.width = width
		lp.height = height
		layoutParams = lp
	}
}

fun View?.onAttach(block: View.() -> Unit) {
	this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
		override fun onViewDetachedFromWindow(v: View) = Unit

		override fun onViewAttachedToWindow(v: View) = v.block()
	})
	if (isAttachedToWindowCompat && this != null) {
		block.invoke(this)
	}
}

fun View?.onDetach(block: View.() -> Unit) {
	this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
		override fun onViewDetachedFromWindow(v: View) = v.block()

		override fun onViewAttachedToWindow(v: View) = Unit
	})
	if (!isAttachedToWindowCompat && this != null) {
		block.invoke(this)
	}
}

internal val View?.isAttachedToWindowCompat: Boolean
	get() {
		return if (this == null) {
			false
		} else {
			ViewCompat.isAttachedToWindow(this)
		}
	}