package com.mvvm.component.uc.recyclerview

import androidx.annotation.LayoutRes

class ItemView {


    private var bindingVariable: Int = 0
    @LayoutRes
    private var layoutRes: Int = 0

    /**
     * A convenience method for `ItemView.setBindingVariable(int).setLayoutRes(int)`.
     *
     * @return the `ItemView` for chaining
     */
    operator fun set(bindingVariable: Int, @LayoutRes layoutRes: Int): ItemView {
        this.bindingVariable = bindingVariable
        this.layoutRes = layoutRes
        return this
    }

    /**
     * Sets the binding variable. This is one of the `BR` constants that references the
     * variable tag in the item layout file.
     *
     * @return the `ItemView` for chaining
     */
    fun setBindingVariable(bindingVariable: Int): ItemView {
        this.bindingVariable = bindingVariable
        return this
    }

    /**
     * Sets the layout resource of the item.
     *
     * @return the `ItemView` for chaining
     */
    fun setLayoutRes(@LayoutRes layoutRes: Int): ItemView {
        this.layoutRes = layoutRes
        return this
    }

    fun bindingVariable(): Int {
        return bindingVariable
    }

    @LayoutRes
    fun layoutRes(): Int {
        return layoutRes
    }

    override fun equals(any: Any?): Boolean {
        if (this === any) {
            return true
        }
        if (any == null || javaClass != any.javaClass) {
            return false
        }

        val itemView = any as ItemView?
        return bindingVariable == itemView!!.bindingVariable && layoutRes == itemView.layoutRes
    }

    override fun hashCode(): Int {
        var result = bindingVariable
        result = 31 * result + layoutRes
        return result
    }

    companion object {
        /**
         * Use this constant as the `bindingVariable` to not bind any variable to the layout. This
         * is useful if no data is needed for that layout, like a static footer or loading indicator for
         * example.
         */
        val BINDING_VARIABLE_NONE = 0

        /**
         * Constructs a new `ItemView` with the given binding variable and layout res.
         *
         * @see .setBindingVariable
         * @see .setLayoutRes
         */
        fun of(bindingVariable: Int, @LayoutRes layoutRes: Int): ItemView {
            return ItemView()
                    .setBindingVariable(bindingVariable)
                    .setLayoutRes(layoutRes)
        }
    }
}
