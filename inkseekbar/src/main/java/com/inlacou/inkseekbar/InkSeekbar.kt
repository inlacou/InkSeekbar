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

class InkSeekbar: FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) { readAttrs(attrSet) }
	constructor(context: Context, attrSet: AttributeSet, arg: Int) : super(context, attrSet, arg) { readAttrs(attrSet) }
	
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
			field = if(value>maxProgress) maxProgress
			else value
			updateDimensions()
		}
	var secondaryProgress = 0
		set(value) {
			field = if(value>maxProgress) maxProgress
			else value
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
	
	var generalCornerRadii: List<Float>? = null
	val backgroundColors: MutableList<Int> = mutableListOf()
	var backgroundOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var backgroundCornerRadii: List<Float>? = null
	val primaryColors: MutableList<Int> = mutableListOf()
	var primaryOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var primaryCornerRadii: List<Float>? = null
	var primaryMargin: Float = 0f
	val secondaryColors: MutableList<Int> = mutableListOf()
	var secondaryOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var secondaryCornerRadii: List<Float>? = null
	var secondaryMargin: Float = 0f
	val markerColors: MutableList<Int> = mutableListOf()
	var markerOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
	var markerCornerRadii: MutableList<Float> = mutableListOf()
	
	//TODO add code to make this into a seekbar (optional ofc, it must also be able to work as a progressbar)
	
	protected open fun readAttrs(attrs: AttributeSet) {
		Log.d("InkSeekbar", "readAttrs")
		val ta = context.obtainStyledAttributes(attrs, R.styleable.InkSeekbar, 0, 0)
		try {
			if (ta.hasValue(R.styleable.InkSeekbar_lineWidth)) {
				lineWidth = ta.getInt(R.styleable.InkSeekbar_lineWidth, lineWidth)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_maxProgress)) {
				maxProgress = ta.getInt(R.styleable.InkSeekbar_maxProgress, maxProgress)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryProgress)) {
				primaryProgress = ta.getInt(R.styleable.InkSeekbar_primaryProgress, primaryProgress)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryProgress)) {
				secondaryProgress = ta.getInt(R.styleable.InkSeekbar_secondaryProgress, secondaryProgress)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_orientation)) {
				orientation = values()[ta.getInt(R.styleable.InkSeekbar_orientation, LEFT_RIGHT.ordinal)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryMargin)) {
				primaryMargin = ta.getDimension(R.styleable.InkSeekbar_primaryMargin, primaryMargin)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryMargin)) {
				secondaryMargin = ta.getDimension(R.styleable.InkSeekbar_secondaryMargin, secondaryMargin)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_corners)) {
				val aux = ta.getDimension(R.styleable.InkSeekbar_corners, -10f)
				if(aux!=-10f) {
					generalCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_backgroundCorners)) {
				val aux = ta.getDimension(R.styleable.InkSeekbar_backgroundCorners, -10f)
				if(aux!=-10f) {
					backgroundCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryCorners)) {
				val aux = ta.getDimension(R.styleable.InkSeekbar_primaryCorners, -10f)
				if(aux!=-10f) {
					primaryCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryCorners)) {
				val aux = ta.getDimension(R.styleable.InkSeekbar_secondaryCorners, -10f)
				if(aux!=-10f) {
					secondaryCornerRadii = listOf(aux)
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_backgroundColor)) {
				val aux = ta.getColor(R.styleable.InkSeekbar_backgroundColor, -1)
				if(aux!=-1) {
					backgroundColors.apply { clear(); add(aux) }
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryColor)) {
				val aux = ta.getColor(R.styleable.InkSeekbar_primaryColor, -1)
				if(aux!=-1) {
					primaryColors.apply { clear(); add(aux) }
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryColor)) {
				val aux = ta.getColor(R.styleable.InkSeekbar_secondaryColor, -1)
				if(aux!=-1) {
					secondaryColors.apply { clear(); add(aux) }
				}
			}
			
		} finally {
			ta.recycle()
		}
		updateDimensions()
		updateDimensions2()
		updateColors()
	}
	
	init {
		Log.d("InkSeekbar", "init")
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
				centerHorizontal(background)
				centerHorizontal(progressPrimary)
				centerHorizontal(progressSecondary)
				if(orientation==TOP_DOWN) {
					alignParentTop(progressPrimary)
					alignParentTop(progressSecondary)
				}else{
					alignParentBottom(progressPrimary)
					alignParentBottom(progressSecondary)
				}
				background?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.width  = lineWidth
				progressPrimary?.layoutParams?.width   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondary?.layoutParams?.width = (lineWidth-(secondaryMargin*2)).toInt()
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				centerVertical(background)
				centerVertical(progressPrimary)
				centerVertical(progressSecondary)
				if(orientation==LEFT_RIGHT) {
					alignParentLeft(progressPrimary)
					alignParentLeft(progressSecondary)
				}else{
					alignParentRight(progressPrimary)
					alignParentRight(progressSecondary)
				}
				background?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				background?.layoutParams?.height = lineWidth
				progressPrimary?.layoutParams?.height   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondary?.layoutParams?.height = (lineWidth-(secondaryMargin*2)).toInt()
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
					progressPrimary?.setMargins(top = (primaryMargin+secondaryMargin).toInt())
					progressSecondary?.setMargins(top = (secondaryMargin).toInt())
				}
				DOWN_TOP -> {
					progressPrimary?.setMargins(bottom = (primaryMargin+secondaryMargin).toInt())
					progressSecondary?.setMargins(bottom = (secondaryMargin).toInt())
				}
				LEFT_RIGHT -> {
					progressPrimary?.setMargins(left = (primaryMargin+secondaryMargin).toInt())
					progressSecondary?.setMargins(left = (secondaryMargin).toInt())
				}
				RIGHT_LEFT -> {
					progressPrimary?.setMargins(right = (primaryMargin+secondaryMargin).toInt())
					progressSecondary?.setMargins(right = (secondaryMargin).toInt())
				}
			}
			progressPrimary?.requestLayout()
			progressSecondary?.requestLayout()
		}
	}
	
	fun updateColors() {
		updateColors(background, backgroundOrientation, backgroundColors, backgroundCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressPrimary, primaryOrientation, primaryColors, primaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressSecondary, secondaryOrientation, secondaryColors, secondaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
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
	
	private fun centerHorizontal(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
			}
		}
	}
	
	private fun centerVertical(view: View?){
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
			}
		}
	}
	
	private fun alignParentTop(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
			}
		}
	}
	
	private fun alignParentBottom(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
			}
		}
	}
	
	private fun alignParentLeft(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
			}
		}
	}
	
	private fun alignParentRight(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
			}
		}
	}
	
}

enum class Orientation {
	TOP_DOWN,
	DOWN_TOP,
	LEFT_RIGHT,
	/** Not working */ RIGHT_LEFT
}