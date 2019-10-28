package com.inlacou.inkseekbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.inlacou.inkseekbar.Orientation.*

class InkSeekbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: FrameLayout(context, attrs, defStyleAttr) {
	
	private var background: View? = null
	private var progressPrimary: View? = null
	private var progressSecondary: View? = null
	private var marker: View? = null
	
	var lineWidth = 20
		set(value) {
			field = value
			update()
		}
	var primaryProgress = 0
		set(value) {
			field = if(value>maxProgress){
				maxProgress
			}else{
				value
			}
			update()
		}
	var secondaryProgress = 0
		set(value) {
			field = if(value>maxProgress){
				maxProgress
			}else{
				value
			}
			update()
		}
	var maxProgress = 300
		set(value) {
			field = value
			update()
		}
	var orientation = LEFT_RIGHT
	val primaryPercentage get() =  primaryProgress.toDouble()/maxProgress
	val secondaryPercentage get() = secondaryProgress.toDouble()/maxProgress
	
	init {
		val rootView = View.inflate(context, R.layout.ink_seekbar, this)
		background = rootView.findViewById(R.id.background)
		progressPrimary = rootView.findViewById(R.id.progress_primary)
		progressSecondary = rootView.findViewById(R.id.progress_secondary)
		marker = rootView.findViewById(R.id.marker)
		background?.let {
			it.onDrawn(false) {
				update2()
			}
		}
		update()
	}
	
	private fun update() {
		Log.d("InkSeekbar", "primaryPercentage $primaryProgress/$maxProgress $primaryPercentage")
		Log.d("InkSeekbar", "secondaryPercentage $secondaryProgress/$maxProgress $secondaryPercentage")
		when(orientation) {
			TOP_DOWN, DOWN_TOP -> {
				Log.d("InkSeekbar", "VERTICAL")
				centerVertical(background)
				centerVertical(progressPrimary)
				centerVertical(progressSecondary)
				background?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.width  = lineWidth
				progressPrimary?.layoutParams?.width   = lineWidth
				progressSecondary?.layoutParams?.width = lineWidth
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				Log.d("InkSeekbar", "HORIZONTAL")
				centerHorizontal(background)
				centerHorizontal(progressPrimary)
				centerHorizontal(progressSecondary)
				background?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.height = lineWidth
				progressPrimary?.layoutParams?.height   = lineWidth
				progressSecondary?.layoutParams?.height = lineWidth
			}
		}
		update2()
	}
	
	private fun update2(){
		background?.let {
			when (orientation) {
				TOP_DOWN, DOWN_TOP -> {
					Log.d("InkSeekbar", "background height: ${it.height}")
					Log.d("InkSeekbar", "primary height: ${(it.height * primaryPercentage).toInt()}")
					Log.d("InkSeekbar", "secondary height: ${(it.height * secondaryPercentage).toInt()}")
					val newPrimary = (it.height * primaryPercentage).toInt()
					val newSecondary = (it.height * secondaryPercentage).toInt()
					if (progressPrimary?.layoutParams?.height != newPrimary) progressPrimary?.layoutParams?.height = newPrimary
					if (progressSecondary?.layoutParams?.height != newPrimary) progressSecondary?.layoutParams?.height = newSecondary
				}
				LEFT_RIGHT, RIGHT_LEFT -> {
					Log.d("InkSeekbar", "background width: ${it.width}")
					Log.d("InkSeekbar", "primary width: ${(it.width * primaryPercentage).toInt()}")
					Log.d("InkSeekbar", "secondary width: ${(it.width * secondaryPercentage).toInt()}")
					val newPrimary = (it.width * primaryPercentage).toInt()
					val newSecondary = (it.width * secondaryPercentage).toInt()
					if (progressPrimary?.layoutParams?.width != newPrimary) progressPrimary?.layoutParams?.width = newPrimary
					if (progressSecondary?.layoutParams?.width != newPrimary) progressSecondary?.layoutParams?.width = newSecondary
				}
			}
			progressPrimary?.requestLayout()
			progressSecondary?.requestLayout()
		}
	}
	
	private fun centerVertical(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
			}
		}
	}
	
	private fun centerHorizontal(view: View?){
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
			}
		}
	}
	
}

enum class Orientation {
	TOP_DOWN, DOWN_TOP, LEFT_RIGHT, RIGHT_LEFT
}