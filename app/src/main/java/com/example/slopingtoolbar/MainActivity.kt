package com.example.slopingtoolbar

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.updateLayoutParams
import androidx.viewpager.widget.ViewPager
import com.example.slopingtoolbar.FragmentMetadata.FabBehaviour
import com.example.slopingtoolbar.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import java.util.Locale

class MainActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, ViewPager.OnPageChangeListener {
	lateinit var binding: ActivityMainBinding
	lateinit var pagerAdapter: MainPagerAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		initToolbar()
		initView()
		initViewPager()
	}

	override fun onResume() {
		super.onResume()

		binding.appBar.addOnOffsetChangedListener(this)
		binding.viewPager.addOnPageChangeListener(this)

		onPageSelected(binding.viewPager.currentItem)
		updateFabIcon(binding.viewPager.currentItem)

	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.getBoolean("appbarCollapsed", isAppbarCollapsed)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		isAppbarCollapsed = savedInstanceState.getBoolean("appbarCollapsed")
	}

	private fun initToolbar() {
		setSupportActionBar(binding.toolbar)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_close)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = null

		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		window.statusBarColor = Color.argb(120, 0, 0, 0)
	}

	private fun initView() {
		// Getting Device screen size
		val size = windowManager.defaultDisplay.let { display -> Point().also { display.getSize(it) } }
		screenHeight = size.y
		screenWidth = size.x

		// Creating LayerPrint to activate anti alias
		val layerPaint = Paint().apply {
			isAntiAlias = true
			isFilterBitmap = true
			isDither = true
		}

		binding.coverContainer.setLayerType(View.LAYER_TYPE_HARDWARE, layerPaint)
		binding.tabsContainer.setLayerType(View.LAYER_TYPE_HARDWARE, layerPaint)

		// Calculating app bar rotation
		var rotation = resources.getInteger(R.integer.toolbar_rotation)

		val coverPivotX: Int
		val tabsPivotX: Int
		if (rtl) {
			rotation *= -1

			coverPivotX = screenWidth - resources.getDimensionPixelSize(R.dimen.main_coverContainer_endMargin)
			tabsPivotX = coverPivotX + resources.getDimensionPixelSize(R.dimen.main_tabsContainer_horizontalMargin)
		} else {
			tabsPivotX = -resources.getDimensionPixelSize(R.dimen.main_tabsContainer_horizontalMargin)
			coverPivotX = 0
		}

		binding.coverContainer.pivotY = resources.getDimensionPixelSize(R.dimen.toolbar_height).toFloat()
		binding.tabsContainer.pivotY = resources.getDimensionPixelSize(android.R.dimen.app_icon_size).toFloat()

		binding.coverContainer.pivotX = coverPivotX.toFloat()
		binding.tabsContainer.pivotX = tabsPivotX.toFloat()

		binding.coverContainer.rotation = rotation.toFloat()
		binding.tabsContainer.rotation = rotation.toFloat()

//		binding.txtOrganizationName.text = getString(R.string.app_name)
//		binding.txtLocationName.text = getString(R.string.text_location)
//		binding.txtWelcomeMessage.text = getString(R.string.text_welcome)
		binding.imgCover.setImageResource(R.drawable.wallpaper)
	}

	private fun initViewPager() {
		// Adjusting ViewPager's top margin for pre-lollipop devices
		binding.viewPager.updateLayoutParams<MarginLayoutParams> {
			val marginTop = resources.getDimensionPixelSize(R.dimen.main_viewPager_topMargin)
			setMargins(0, marginTop, 0, 0)
		}

		// Creating new pager adapter and assigning it to view pager
		pagerAdapter = MainPagerAdapter(this, supportFragmentManager)
		binding.viewPager.setAdapter(pagerAdapter)

		// Increasing the off screen page limit to stop recycling pages and speed the pager up
		binding.viewPager.setOffscreenPageLimit(3)

		// Lining TabLayout with out ViewPager to insure tab synchronization
		binding.tabLayout.setupWithViewPager(binding.viewPager)

		// Setting tabs icons and hints
		for (x in 0 until binding.tabLayout.tabCount) {
			val metadata = pagerAdapter.getMetadata(x) ?: continue

			binding.tabLayout.getTabAt(x)?.also {
				it.setText(null)
				it.setIcon(metadata.tabIcon)
			}

			val tabView = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(x)
			tabView.contentDescription = getString(metadata.tabTip)
			TooltipCompat.setTooltipText(tabView, tabView.contentDescription)
		}

		binding.viewPager.setCurrentItem(0)
	}

	private val rtl: Boolean
		get() {
			val directionality = Character.getDirectionality(Locale.getDefault().displayName[0]).toInt()
			return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
		}

	//region OnOffsetChangedListener Interface
	private var isAppbarCollapsed = false
	private var screenHeight = 0
	private var screenWidth = 0

	private fun calculateFabMaxTop(): Int {
		val tv = TypedValue()
		var actionBarSize = 150
		if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
			actionBarSize = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)

		return screenHeight - actionBarSize
	}

	override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
		isAppbarCollapsed = verticalOffset != 0

		val maxOffset = (appBarLayout.totalScrollRange / 2).toFloat()
		val metadata = pagerAdapter.getMetadata(binding.viewPager.currentItem)

		val offset = maxOffset + verticalOffset
		var firstStepOffset = offset
		var secondStepOffset = -offset

		if (offset < 0) {
			firstStepOffset = 0f

			if (binding.btnFAB.isShown() && !metadata!!.isFabAlwaysVisible) binding.btnFAB.hide()
		} else {
			secondStepOffset = 0f

			if (!binding.btnFAB.isShown()) binding.btnFAB.show()
		}

		// Stuff to do in the first step
		// Rotating the cover image and the tabs
		val maxRotation = resources.getInteger(R.integer.toolbar_rotation)
		var newRotation = maxRotation * firstStepOffset / maxOffset
		if (rtl) newRotation *= -1f
		binding.coverContainer.rotation = newRotation
		binding.tabsContainer.rotation = newRotation

		// Correcting the float action button's location
		val maxFabMargin = resources.getDimensionPixelSize(R.dimen.main_fab_bottomMargin)
		val newFabMargin = (maxFabMargin * firstStepOffset / maxOffset).toInt()
		binding.fabContainer.setPaddingRelative(0, 0, 0, newFabMargin)

		// Stuff to do in the second step
		// Centering the tabs
		val tabsMargin = resources.getDimensionPixelSize(R.dimen.main_tabsContainer_endPadding)
		val maxTabsMargin = tabsMargin / 2
		val newTabsMargin = (maxTabsMargin * secondStepOffset / maxOffset).toInt()
		binding.tabsContainer.setPaddingRelative(newTabsMargin, 0, tabsMargin - newTabsMargin, 0)

		// Adjusting tabs background
		val maxAlpha = 92
		var newAlpha = (maxAlpha * secondStepOffset / maxOffset).toInt()
		newAlpha = maxAlpha - newAlpha
		binding.tabsContainer.setBackgroundColor(Color.argb(newAlpha, 0, 0, 0))

		// Making scrim effect
		val maxScrim = 1f
		val newScrim = maxScrim * secondStepOffset / maxOffset
		binding.scrimView.setAlpha(newScrim)

		// Moving the fab downward
		if (metadata!!.isFabAlwaysVisible) {
			val maxTop = calculateFabMaxTop()
			val newTop = (maxTop * secondStepOffset / maxOffset).toInt()
			binding.fabContainer.setPaddingRelative(0, newTop, 0, binding.fabContainer.paddingBottom)
		}
	}
	//endregion

	//region OnPageChangeListener Interface
	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
	}

	override fun onPageSelected(position: Int) {
		val metadata = pagerAdapter.getMetadata(position)
		binding.btnFAB.setContentDescription(getString(metadata!!.fabTip))
		TooltipCompat.setTooltipText(binding.btnFAB, binding.btnFAB.contentDescription)

		binding.btnFAB.setOnClickListener(
			when (metadata.fabBehaviour) {
				FabBehaviour.CALL -> onCallClick
				FabBehaviour.CART -> onCartClick
			}
		)

		if (isAppbarCollapsed) {
			if (metadata.isFabAlwaysVisible) {
				if (!binding.btnFAB.isShown()) {
					binding.fabContainer.setPaddingRelative(0, calculateFabMaxTop(), 0, binding.fabContainer.paddingBottom)

					updateFabIcon(position)
					binding.btnFAB.show()
				}
			} else {
				if (binding.btnFAB.isShown()) {
					binding.btnFAB.hide(object : OnVisibilityChangedListener() {
						override fun onHidden(fab: FloatingActionButton) {
							super.onHidden(fab)
							updateFabIcon(position)
							binding.fabContainer.setPaddingRelative(0, 0, 0, binding.fabContainer.paddingBottom)
						}
					})
				}
			}
		} else updateFabIcon(position)
	}

	private fun updateFabIcon(pos: Int) {
		val metadata = pagerAdapter.getMetadata(pos)
		binding.btnFAB.setImageResource(metadata!!.fabIcon)
	}

	override fun onPageScrollStateChanged(state: Int) {
	}

	private val onCallClick = View.OnClickListener {
	}

	private val onCartClick = View.OnClickListener {
	}
	//endregion
}