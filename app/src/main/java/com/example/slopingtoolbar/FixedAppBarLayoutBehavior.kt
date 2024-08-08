package com.example.slopingtoolbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

class FixedAppBarLayoutBehavior : AppBarLayout.Behavior {
	private var mStartedScrollType = -1
	private var mSkipNextStop = false

	constructor() : super()

	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
		if (mStartedScrollType != -1) {
			onStopNestedScroll(parent, child, target, mStartedScrollType)
			mSkipNextStop = true
		}
		mStartedScrollType = type
		return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
	}

	override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
		if (mSkipNextStop) {
			mSkipNextStop = false
			return
		}
		if (mStartedScrollType == -1) {
			return
		}
		mStartedScrollType = -1
		// Always pass TYPE_TOUCH, because want to snap even after fling
		super.onStopNestedScroll(coordinatorLayout, abl, target, ViewCompat.TYPE_TOUCH)
	}
}
