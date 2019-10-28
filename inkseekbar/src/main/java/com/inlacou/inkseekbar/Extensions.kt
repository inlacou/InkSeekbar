package com.inlacou.inkseekbar

import android.util.Log
import android.view.View
import android.view.ViewTreeObserver

fun View.onDrawn(continuous: Boolean = false, callback: () -> Unit) {
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
