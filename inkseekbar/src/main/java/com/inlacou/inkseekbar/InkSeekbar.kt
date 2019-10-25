package com.inlacou.inkseekbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.inlacou.inkseekbar.Orientation.*

class InkSeekbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: FrameLayout(context, attrs, defStyleAttr) {
	
	private var background: View? = null
	private var progressPrimary: View? = null
	private var progressSecondary: View? = null
	private var marker: View? = null
	
	var lineWidth = 10
		set(value) {
			field = value
			update()
		}
	var primaryProgress = 0
		set(value) {
			field = value
			update()
		}
	var secondaryProgress = 0
		set(value) {
			field = value
			update()
		}
	var maxProgress = 100
		set(value) {
			field = value
			update()
		}
	var orientation = LEFT_RIGHT
	
	init {
		val rootView = View.inflate(context, R.layout.ink_seekbar, this)
		background = rootView.findViewById(R.id.background)
		progressPrimary = rootView.findViewById(R.id.progress_primary)
		progressSecondary = rootView.findViewById(R.id.progress_secondary)
		marker = rootView.findViewById(R.id.marker)
	}
	
	private fun update() {
		val primaryPercentage = primaryProgress.toDouble()*100/maxProgress
		val secondaryPercentage = secondaryProgress.toDouble()*100/maxProgress
		when(orientation) {
			TOP_DOWN, DOWN_TOP -> {
				centerVertical(background)
				background?.layoutParams?.height = LayoutParams.MATCH_PARENT
				centerVertical(progressPrimary)
				centerVertical(progressSecondary)
				background?.layoutParams?.width = lineWidth
				progressPrimary?.layoutParams?.width = lineWidth
				progressSecondary?.layoutParams?.width = lineWidth
				progressPrimary?.layoutParams?.height = (height*primaryPercentage).toInt()
				progressSecondary?.layoutParams?.height = (height*secondaryPercentage).toInt()
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				centerHorizontal(background)
				background?.layoutParams?.width = LayoutParams.MATCH_PARENT
				centerHorizontal(progressPrimary)
				background?.layoutParams?.height = lineWidth
				progressPrimary?.layoutParams?.height = lineWidth
				progressSecondary?.layoutParams?.height = lineWidth
				progressPrimary?.layoutParams?.width = (width*primaryPercentage).toInt()
				progressSecondary?.layoutParams?.width = (width*secondaryPercentage).toInt()
				centerHorizontal(progressSecondary)
			}
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