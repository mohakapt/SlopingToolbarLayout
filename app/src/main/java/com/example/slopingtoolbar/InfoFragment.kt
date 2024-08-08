package com.example.slopingtoolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.example.slopingtoolbar.FragmentMetadata.FabBehaviour

class InfoFragment : Fragment() {
	companion object {
		@Keep
		@JvmField
		val METADATA = FragmentMetadata(
			title = "InfoFragment",
			tabIcon = R.drawable.ic_info,
			tabTip = R.string.toast_info,

			fabIcon = R.drawable.ic_action_call,
			fabTip = R.string.toast_call,
			fabBehaviour = FabBehaviour.CALL,
			isFabAlwaysVisible = false,
		)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_info, container, false)
	}
}