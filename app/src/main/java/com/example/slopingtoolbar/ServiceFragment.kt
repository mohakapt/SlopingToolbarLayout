package com.example.slopingtoolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.example.slopingtoolbar.FragmentMetadata.FabBehaviour

class ServiceFragment : Fragment() {
	companion object {
		@Keep
		@JvmField
		val METADATA = FragmentMetadata(
			title = "ServiceFragment",
			tabIcon = R.drawable.ic_services,
			tabTip = R.string.toast_services,

			fabIcon = R.drawable.ic_action_call,
			fabTip = R.string.toast_call,
			fabBehaviour = FabBehaviour.CALL,
			isFabAlwaysVisible = false,
		)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_services, container, false)
	}
}