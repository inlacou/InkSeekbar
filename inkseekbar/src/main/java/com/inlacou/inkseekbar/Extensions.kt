package com.inlacou.inkseekbar

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

internal fun View.onDrawn(continuous: Boolean = false, callback: () -> Unit) {
	Log.d("onDrawn", "init")
	Log.d("onDrawn", "viewTreeObserver: $viewTreeObserver")
	viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			Log.d("onDrawn", "fired")
			if(!continuous) {
				Log.d("onDrawn", "clear")
				viewTreeObserver?.removeOnGlobalLayoutListener(this)
			}
			callback.invoke()
		}
	})
}

internal fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
	if (layoutParams is ViewGroup.MarginLayoutParams) {
		val p = layoutParams as ViewGroup.MarginLayoutParams
		Log.d("margins", "left: $left right: $right top: $top bottom $bottom")
		p.setMargins(left ?: p.leftMargin, top ?: p.topMargin, right ?: p.rightMargin, bottom ?: p.bottomMargin)
		layoutParams = p
		requestLayout()
	}
}
