package com.inlacou.inkseekbar

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout

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

internal fun View.setBackgroundCompat(drawable: Drawable){
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		this.background = drawable
	}else{
		this.setBackgroundDrawable(drawable)
	}
}

internal fun View.centerHorizontal() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0)
		}
	}
}

internal fun View.centerVertical(){
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0)
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
		}
	}
}

internal fun View.alignParentTop() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
		}
	}
}

internal fun View.alignParentBottom() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
		}
	}
}

internal fun View.alignParentLeft() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
		}
	}
}

internal fun View.alignParentRight() {
	layoutParams?.let { layoutParams ->
		if(layoutParams is RelativeLayout.LayoutParams){
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
		}
	}
}

/*
enum class ShowType {
	FROM_TOP_TO_BOTTOM, FROM_RIGHT_TO_LEFT, FROM_BOTTOM_TO_TOP, FROM_LEFT_TO_RIGHT, FADE_IN
}

fun View.animate(duration: Long, showType: ShowType, easeType: EaseType) {
	//DOES NOT WORK IF VIEW IS GONE, following line does not work
	this.visibility = INVISIBLE
	Log.d("translationX", "" + translationX)
	Log.d("translationY", "" + translationY)
	Log.d("height", "" + height)
	Log.d("width", "" + width)
	val animator = when (showType) {
		ShowType.FROM_TOP_TO_BOTTOM -> ObjectAnimator.ofFloat(this, "translationY", translationY - height, translationY)
		ShowType.FROM_RIGHT_TO_LEFT -> ObjectAnimator.ofFloat(this, "translationX", translationX + width, translationX)
		ShowType.FROM_BOTTOM_TO_TOP -> ObjectAnimator.ofFloat(this, "translationY", translationY + height, translationY)
		ShowType.FROM_LEFT_TO_RIGHT -> ObjectAnimator.ofFloat(this, "translationX", translationX - width, translationX)
		ShowType.FADE_IN -> ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
	}
	when (showType) {
		ShowType.FROM_TOP_TO_BOTTOM -> Log.d("translationY", "" + (translationY - height) + " to " + translationY)
		ShowType.FROM_RIGHT_TO_LEFT -> Log.d("translationX", "" + (translationX + width) + " to " + translationX)
		ShowType.FROM_BOTTOM_TO_TOP -> Log.d("translationY", "" + (translationY + height) + " to " + translationY)
		ShowType.FROM_LEFT_TO_RIGHT -> Log.d("translationX", "" + (translationX - width) + " to " + translationX)
		ShowType.FADE_IN -> Log.d("alpha", "" + (0f) + "to" + 1f)
	}
	visibility = VISIBLE
	animator.duration = duration
	animator.addUpdateListener { invalidate() }
	animator.addListener(object : AnimatorListenerAdapter() {
		override fun onAnimationStart(animation: Animator?) {
			super.onAnimationStart(animation)
			
		}
		
		override fun onAnimationEnd(animation: Animator?) {
			super.onAnimationEnd(animation)
			
		}
	})
	animator.interpolator = InterpolatorFactory.getInterpolator(easeType)
	animator.start()
}*/