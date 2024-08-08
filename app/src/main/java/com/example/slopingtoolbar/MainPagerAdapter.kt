package com.example.slopingtoolbar

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Created by MoHaKa on 7/17/16.
 */
class MainPagerAdapter(private val mContext: Context, manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
	private val fragmentNames = mutableListOf(
		ServiceFragment::class.java.getName(),
		MenuFragment::class.java.getName(),
		InfoFragment::class.java.getName()
	)

	fun getMetadata(pos: Int): FragmentMetadata? {
		var reVal: FragmentMetadata? = null
		try {
			val clazz = Class.forName(fragmentNames[pos])
			reVal = clazz.getDeclaredField("METADATA")[null] as FragmentMetadata
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return reVal
	}

	override fun getItem(position: Int): Fragment {
		return Fragment.instantiate(mContext, fragmentNames[position])
	}

	override fun getCount(): Int {
		return fragmentNames.size
	}

	override fun getPageTitle(position: Int): CharSequence? {
		return null
	}
}