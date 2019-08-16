package com.mvvm.component.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by FlameYagami on 2017/5/3.
 */

class BaseFragmentAdapter(manager: FragmentManager, private val classList: List<Class<*>>) :
    FragmentPagerAdapter(manager) {

    private var fragments: Array<Fragment?> = arrayOfNulls(classList.size)

    override fun getItem(position: Int): Fragment {
        return fragments[position] ?: let {
            fragments[position] = Class.forName(classList[position].name).newInstance() as Fragment
            fragments[position]!!
        }
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
