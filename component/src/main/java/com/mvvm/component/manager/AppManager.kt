package com.mvvm.component.manager

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.*

class AppManager {

    var stack: Stack<WeakReference<Activity>> = Stack()

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppManager()
        }

        /**
         * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
         */
        fun clearWeakReference(stack: Stack<WeakReference<Activity>>) {
            synchronized(this) {
                with(stack.iterator()) {
                    while (hasNext()) {
                        next().get() ?: apply { remove() }
                    }
                }
            }
        }

        fun topActivity(): Activity? {
            clearWeakReference(instance.stack)
            return instance.stack.lastElement().get()
        }

        /**
         * 添加Activity到堆栈
         */
        fun addActivity(activity: Activity) {
            instance.stack.add(WeakReference(activity))
        }

        /**
         * 结束当前Activity（堆栈中最后一个压入的）
         */
        fun finishTopActivity() {
            clearWeakReference(instance.stack)
            if (0 == instance.stack.size) return
            instance.stack.lastElement().get()?.apply {
                finishActivity(this)
            }
        }

        /**
         * 结束指定的Activity
         */
        fun finishActivity(activity: Activity) {
            clearWeakReference(instance.stack)
            with(instance.stack.iterator()) {
                while (hasNext()) {
                    next().get()?.takeIf {
                        it == activity
                    }?.apply {
                        finish()
                        remove()
                    }
                }
            }
        }

        /**
         * 结束指定类名的Activity
         */
        inline fun <reified T : Activity> finishActivity() {
            clearWeakReference(instance.stack)
            with(instance.stack.iterator()) {
                while (hasNext()) {
                    next().get()?.takeIf {
                        it.javaClass == T::class.java
                    }?.apply {
                        finish()
                        remove()
                    }
                }
            }
        }

        /**
         * 结束所有Activity
         */
        fun finishAllActivity() {
            with(instance.stack) {
                forEach { it.get()?.finish() }
                clear()
            }
        }

        /**
         * 结束所有参数外的所有Activity
         */
        inline fun <reified T : Activity> finishAllActivityExcept() {
            clearWeakReference(instance.stack)
            with(instance.stack.iterator()) {
                while (hasNext()) {
                    next().get()?.takeIf { it.javaClass != T::class.java }?.apply {
                        finish()
                        remove()
                    }
                }
            }
        }
    }
}