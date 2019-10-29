package com.inlacou.inkseekbar

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

internal fun View.onDrawn(continuous: Boolean = false, callback: () -> Unit) {
	viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			if(!continuous) {
				viewTreeObserver?.removeOnGlobalLayoutListener(this)
			}
			callback.invoke()
		}
	})
}

internal fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
	if (layoutParams is ViewGroup.MarginLayoutParams) {
		val p = layoutParams as ViewGroup.MarginLayoutParams
		p.setMargins(left ?: p.leftMargin, top ?: p.topMargin, right ?: p.rightMargin, bottom ?: p.bottomMargin)
		layoutParams = p
		requestLayout()
	}
}
