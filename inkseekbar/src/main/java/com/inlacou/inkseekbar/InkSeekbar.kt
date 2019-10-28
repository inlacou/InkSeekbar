package com.inlacou.inkseekbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.wifi.WifiConfiguration
import android.provider.SyncStateContract.Helpers.update
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
	
	var lineWidth = 100
		set(value) {
			field = value
			updateDimensions()
		}
	var primaryProgress = 0
		set(value) {
			field = if(value>maxProgress){
				maxProgress
			}else{
				value
			}
			updateDimensions()
		}
	var secondaryProgress = 0
		set(value) {
			field = if(value>maxProgress){
				maxProgress
			}else{
				value
			}
			updateDimensions()
		}
	var maxProgress = 300
		set(value) {
			field = value
			updateDimensions()
		}
	var orientation = LEFT_RIGHT
	val primaryPercentage get() =  primaryProgress.toDouble()/maxProgress
	val secondaryPercentage get() = secondaryProgress.toDouble()/maxProgress
	
	val backgroundColors: MutableList<Int> = mutableListOf()
	var backgroundOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var backgroundCornerRadii: MutableList<Float> = mutableListOf()
	val primaryColors: MutableList<Int> = mutableListOf()
	var primaryOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var primaryCornerRadii: MutableList<Float> = mutableListOf()
	var primaryMargin: Int = 0
	val secondaryColors: MutableList<Int> = mutableListOf()
	var secondaryOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var secondaryCornerRadii: MutableList<Float> = mutableListOf()
	var secondaryMargin: Int = 0
	val markerColors: MutableList<Int> = mutableListOf()
	var markerOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var markerCornerRadii: MutableList<Float> = mutableListOf()
	
	//TODO add code to make this into a seekbar (optional ofc, it must also be able to work as a progressbar)
	
	init {
		val rootView = View.inflate(context, R.layout.ink_seekbar, this)
		background = rootView.findViewById(R.id.background)
		progressPrimary = rootView.findViewById(R.id.progress_primary)
		progressSecondary = rootView.findViewById(R.id.progress_secondary)
		marker = rootView.findViewById(R.id.marker)
		background?.let {
			it.onDrawn(false) {
				updateDimensions2()
			}
		}
		updateDimensions()
	}
	
	private fun updateDimensions() {
		when(orientation) {
			TOP_DOWN, DOWN_TOP -> {
				centerVertical(background)
				centerVertical(progressPrimary)
				centerVertical(progressSecondary)
				background?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.width  = lineWidth
				progressPrimary?.layoutParams?.width   = lineWidth-((primaryMargin+secondaryMargin)*2)
				progressSecondary?.layoutParams?.width = lineWidth-(secondaryMargin*2)
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				centerHorizontal(background)
				centerHorizontal(progressPrimary)
				centerHorizontal(progressSecondary)
				background?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.height = lineWidth
				progressPrimary?.layoutParams?.height   = lineWidth-((primaryMargin+secondaryMargin)*2)
				progressSecondary?.layoutParams?.height = lineWidth-(secondaryMargin*2)
			}
		}
		updateDimensions2()
	}
	
	private fun updateDimensions2(){
		background?.let {
			when (orientation) {
				TOP_DOWN, DOWN_TOP -> {
					val newPrimary = ((it.height-((primaryMargin+secondaryMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.height-(secondaryMargin*2)) * secondaryPercentage).toInt()
					if (progressPrimary?.layoutParams?.height != newPrimary) progressPrimary?.layoutParams?.height = newPrimary
					if (progressSecondary?.layoutParams?.height != newPrimary) progressSecondary?.layoutParams?.height = newSecondary
				}
				LEFT_RIGHT, RIGHT_LEFT -> {
					val newPrimary = ((it.width-((primaryMargin+secondaryMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.width-(secondaryMargin*2)) * secondaryPercentage).toInt()
					if (progressPrimary?.layoutParams?.width != newPrimary) progressPrimary?.layoutParams?.width = newPrimary
					if (progressSecondary?.layoutParams?.width != newPrimary) progressSecondary?.layoutParams?.width = newSecondary
				}
			}
			when(orientation){
				TOP_DOWN -> {
					progressPrimary?.setMargins(top = primaryMargin+secondaryMargin)
					progressSecondary?.setMargins(top = secondaryMargin)
				}
				DOWN_TOP -> {
					progressPrimary?.setMargins(bottom = primaryMargin+secondaryMargin)
					progressSecondary?.setMargins(bottom = secondaryMargin)
				}
				LEFT_RIGHT -> {
					progressPrimary?.setMargins(left = primaryMargin+secondaryMargin)
					progressSecondary?.setMargins(left = secondaryMargin)
				}
				RIGHT_LEFT -> {
					progressPrimary?.setMargins(right = primaryMargin+secondaryMargin)
					progressSecondary?.setMargins(right = secondaryMargin)
				}
			}
			progressPrimary?.requestLayout()
			progressSecondary?.requestLayout()
		}
	}
	
	fun updateColors() {
		updateColors(background, backgroundOrientation, backgroundColors, backgroundCornerRadii)
		updateColors(progressPrimary, primaryOrientation, primaryColors, primaryCornerRadii)
		updateColors(progressSecondary, secondaryOrientation, secondaryColors, secondaryCornerRadii)
		updateColors(marker, markerOrientation, markerColors, markerCornerRadii)
	}
	
	private fun updateColors(view: View?, orientation: GradientDrawable.Orientation, colorList: List<Int>, customCornerRadii: List<Float>) {
		val colors = colorList.toMutableList()
		if(colors.size==1) colors.add(colors[0])
		view?.apply {
			when {
				colors.size > 1 -> {
					this.background = GradientDrawable(orientation, colors.toIntArray()).apply {
						this.cornerRadii = when {
							customCornerRadii.size == 1 -> floatArrayOf(
									customCornerRadii[0], customCornerRadii[0], customCornerRadii[0], customCornerRadii[0],
									customCornerRadii[0], customCornerRadii[0], customCornerRadii[0], customCornerRadii[0])
							customCornerRadii.size == 4 -> floatArrayOf(
									customCornerRadii[0], customCornerRadii[0], customCornerRadii[1], customCornerRadii[1],
									customCornerRadii[2], customCornerRadii[2], customCornerRadii[3], customCornerRadii[3])
							customCornerRadii.size == 8 -> floatArrayOf(
									customCornerRadii[0], customCornerRadii[1], customCornerRadii[2], customCornerRadii[3],
									customCornerRadii[4], customCornerRadii[5], customCornerRadii[6], customCornerRadii[7])
							else -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
						}
					}
				}
				colors.size == 1 -> {
					this.setBackgroundColor(colors.size)
				}
				else -> {
					//TODO
				}
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