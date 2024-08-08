package com.example.slopingtoolbar

import androidx.annotation.Keep

/**
 * Created by MoHaKa on 7/17/16.
 */
class FragmentMetadata(
	val title: String,
	val tabIcon: Int,
	val fabIcon: Int,
	val tabTip: Int,
	val fabTip: Int,
	val fabBehaviour: FabBehaviour,
	val isFabAlwaysVisible: Boolean
) {
	@Keep
	enum class FabBehaviour {
		CALL, CART
	}
}