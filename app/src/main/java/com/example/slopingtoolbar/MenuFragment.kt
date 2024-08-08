package com.example.slopingtoolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.example.slopingtoolbar.FragmentMetadata.FabBehaviour

class MenuFragment : Fragment() {
	companion object {
		@Keep
		@JvmField
		val METADATA = FragmentMetadata(
			title = "MenuFragment",
			tabIcon = R.drawable.ic_menu,
			tabTip = R.string.toast_menu,

			fabIcon = R.drawable.ic_action_cart,
			fabTip = R.string.toast_showCart,
			fabBehaviour = FabBehaviour.CART,
			isFabAlwaysVisible = true,
		)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_menu, container, false)
	}
}