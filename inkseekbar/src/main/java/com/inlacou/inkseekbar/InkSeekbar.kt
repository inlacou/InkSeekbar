package com.inlacou.inkseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.inlacou.inkseekbar.Orientation.*
import kotlin.math.roundToInt

class InkSeekbar: FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) { readAttrs(attrSet) }
	constructor(context: Context, attrSet: AttributeSet, arg: Int) : super(context, attrSet, arg) { readAttrs(attrSet) }
	
	private var clickableView: View? = null
	private var backgroundView: View? = null
	private var progressPrimaryView: View? = null
	private var progressSecondaryView: View? = null
	private var markerView: View? = null
	
	var lineWidth = 100f
		set(value) {
			field = value
			updateDimensions()
		}
	var markerWidth = 100f
		set(value) {
			field = value
			updateDimensions()
		}
	var markerHeight = 100f
		set(value) {
			field = value
			updateDimensions()
		}
	
	fun setPrimaryProgress(value: Int, fromUser: Boolean) {
		if(value>maxProgress)
			primaryProgress = maxProgress
		else {
			primaryProgress = value
			if(fromUser) onValueChangeListener?.invoke(value, secondaryProgress)
			onValuePrimaryChangeListener?.invoke(value, fromUser)
			onValuePrimarySetListener?.invoke(value, fromUser)
		}
		//updateDimensions()
	}
	
	fun setSecondaryProgress(value: Int, fromUser: Boolean) {
		if(value>maxProgress)
			secondaryProgress = maxProgress
		else {
			secondaryProgress = value
			if(fromUser) onValueChangeListener?.invoke(primaryProgress, value)
			onValueSecondaryChangeListener?.invoke(value, fromUser)
			onValueSecondarySetListener?.invoke(value, fromUser)
		}
		updateDimensions()
	}
	
	/**
	 * Fired on any value change, primary or secondary. But only if fired by user (or fromUser==true), either for primary or for secondary value change.
	 */
	var onValueChangeListener: ((primary: Int, secondary: Int) -> Unit)? = null
	/**
	 * Fired on any primary value change.
	 */
	var onValuePrimaryChangeListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired on any primary value change.
	 */
	var onValueSecondaryChangeListener: ((secondary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired when user releases touch or when progress is set programmatically
	 */
	var onValuePrimarySetListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired when progress is set programmatically
	 */
	var onValueSecondarySetListener: ((secondary: Int, fromUser: Boolean) -> Unit)? = null
	
	var primaryProgress = 0
		private set
	var secondaryProgress = 0
		private set
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
	var markerCornerRadii: List<Float>? = null
	var mode: Mode = Mode.PROGRESS
	
	private val generalHorizontalMargin: Float get() = if(mode==Mode.PROGRESS) 0f else when (orientation) {
		TOP_DOWN, DOWN_TOP -> {
			val aux = (markerWidth-lineWidth)/2
			if(aux>0) aux else 0f
		}
		LEFT_RIGHT, RIGHT_LEFT -> {
			val aux = (markerWidth/2)-primaryMargin-secondaryMargin
			if(aux>0) aux else 0f
		}
	}
	
	private val generalVerticalMargin: Float get() = if(mode==Mode.PROGRESS) 0f else when (orientation) {
		TOP_DOWN, DOWN_TOP -> {
			val aux = (markerWidth/2)-primaryMargin-secondaryMargin
			if(aux>0) aux else 0f
		}
		LEFT_RIGHT, RIGHT_LEFT -> {
			val aux = (markerWidth-lineWidth)/2
			if(aux>0) aux else 0f
		}
	}
	
	private fun readAttrs(attrs: AttributeSet) {
		val ta = context.obtainStyledAttributes(attrs, R.styleable.InkSeekbar, 0, 0)
		try {
			if (ta.hasValue(R.styleable.InkSeekbar_lineWidth)) {
				lineWidth = ta.getDimension(R.styleable.InkSeekbar_lineWidth, lineWidth)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_markerWidth)) {
				markerWidth = ta.getDimension(R.styleable.InkSeekbar_markerWidth, markerWidth)
			}
			if (ta.hasValue(R.styleable.InkSeekbar_markerHeight)) {
				markerHeight = ta.getDimension(R.styleable.InkSeekbar_markerHeight, markerHeight)
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
			if (ta.hasValue(R.styleable.InkSeekbar_markerCorners)) {
				val aux = ta.getDimension(R.styleable.InkSeekbar_markerCorners, -10f)
				if(aux!=-10f) {
					markerCornerRadii = listOf(aux)
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
			if (ta.hasValue(R.styleable.InkSeekbar_markerColor)) {
				val aux = ta.getColor(R.styleable.InkSeekbar_markerColor, -1)
				if(aux!=-1) {
					markerColors.apply { clear(); add(aux) }
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_backgroundColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSeekbar_backgroundColors, -1)).toList().let {
					if(it.isNotEmpty()) {
						backgroundColors.clear()
						it.forEach { backgroundColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSeekbar_primaryColors, -1)).toList().let {
					if(it.isNotEmpty()) {
						primaryColors.clear()
						it.forEach { primaryColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryColor)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSeekbar_secondaryColor, -1)).toList().let {
					if(it.isNotEmpty()) {
						secondaryColors.clear()
						it.forEach { secondaryColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_markerColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSeekbar_markerColors, -1)).toList().let {
					if(it.isNotEmpty()) {
						markerColors.clear()
						it.forEach { markerColors.add(it) }
					}
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_backgroundGradientOrientation)) {
				backgroundOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_backgroundGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryGradientOrientation)) {
				primaryOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_primaryGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryGradientOrientation)) {
				secondaryOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_secondaryGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_markerGradientOrientation)) {
				markerOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_markerGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_mode)) {
				mode = Mode.values()[ta.getInt(R.styleable.InkSeekbar_mode, 0)]
			}
		} finally {
			ta.recycle()
		}
		updateDimensions()
		updateDimensions2()
		updateColors()
	}
	
	private val totalPrimarySize: Float get() = when(orientation){
		TOP_DOWN, DOWN_TOP -> {
			backgroundView?.let {
				it.height-((primaryMargin+secondaryMargin)*2)
			} ?: 0f
		}
		LEFT_RIGHT, RIGHT_LEFT ->
			backgroundView?.let {
				it.width-((primaryMargin+secondaryMargin)*2)
			} ?: 0f
	}
	
	private val stepSize: Float get() = totalPrimarySize/maxProgress
	private val totalSteps: Int get() = (totalPrimarySize/stepSize).roundToInt()
	
	init {
		val rootView = View.inflate(context, R.layout.ink_seekbar, this)
		clickableView = rootView.findViewById(R.id.clickable)
		backgroundView = rootView.findViewById(R.id.background)
		progressPrimaryView = rootView.findViewById(R.id.progress_primary)
		progressSecondaryView = rootView.findViewById(R.id.progress_secondary)
		markerView = rootView.findViewById(R.id.marker)
		backgroundView?.let {
			it.onDrawn(false) {
				updateDimensions2()
			}
		}
		setListeners()
		updateDimensions()
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		clickableView?.setOnTouchListener { _, event ->
			if(mode==Mode.PROGRESS) return@setOnTouchListener false
			
			val relativePosition = when(orientation) {
				TOP_DOWN, DOWN_TOP -> event.y
				LEFT_RIGHT, RIGHT_LEFT -> event.x
			} //reaches 0 at top and goes on the minus realm if you keep going up
			var fixedRelativePosition = relativePosition-(primaryMargin+secondaryMargin+generalHorizontalMargin) //Fix touch
			if(fixedRelativePosition<0) fixedRelativePosition = 0f
			if(fixedRelativePosition>totalPrimarySize) fixedRelativePosition = totalPrimarySize
			//val roughStep = if(reversed) (fixedRelativePosition/stepSize)-1 else (fixedRelativePosition/stepSize)
			val newPosition = if(orientation==DOWN_TOP || orientation==RIGHT_LEFT) (totalSteps-(fixedRelativePosition/stepSize)).roundToInt() else (fixedRelativePosition/stepSize).roundToInt()
			//val newPosition = (fixedRelativePosition/stepSize).roundToInt()
			if(primaryProgress!=newPosition) {
				onValueChangeListener?.invoke(primaryProgress, secondaryProgress)
				onValuePrimaryChangeListener?.invoke(primaryProgress, true)
			}
			primaryProgress = newPosition
			onValueChangeListener?.invoke(newPosition, secondaryProgress)
			onValuePrimaryChangeListener?.invoke(newPosition, true)
			updateDimensions()
			
			when(event.action){
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					true
				}
				MotionEvent.ACTION_CANCEL -> false
				MotionEvent.ACTION_UP -> {
					onValuePrimarySetListener?.invoke(primaryProgress, true)
					//TODO fire listener release (user interaction true)
					false
				}
				MotionEvent.ACTION_MOVE -> true
				else -> false
			}
		}
	}
	
	private fun updateDimensions() {
		when(orientation) {
			TOP_DOWN, DOWN_TOP -> {
				centerHorizontal(clickableView)
				centerHorizontal(backgroundView)
				centerHorizontal(progressPrimaryView)
				centerHorizontal(progressSecondaryView)
				centerHorizontal(markerView)
				if(orientation==TOP_DOWN) {
					alignParentTop(progressPrimaryView)
					alignParentTop(progressSecondaryView)
					alignParentTop(markerView)
				}else{
					alignParentBottom(progressPrimaryView)
					alignParentBottom(progressSecondaryView)
					alignParentBottom(markerView)
				}
				markerView?.layoutParams?.width = if(mode==Mode.SEEKBAR) markerWidth.roundToInt() else 0
				markerView?.layoutParams?.height = if(mode==Mode.SEEKBAR) markerHeight.roundToInt() else 0
				clickableView?.layoutParams?.width  = lineWidth.roundToInt()
				clickableView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.width  = lineWidth.roundToInt()
				backgroundView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				progressPrimaryView?.layoutParams?.width   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.width = (lineWidth-(secondaryMargin*2)).toInt()
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				centerVertical(clickableView)
				centerVertical(backgroundView)
				centerVertical(progressPrimaryView)
				centerVertical(progressSecondaryView)
				centerVertical(markerView)
				if(orientation==LEFT_RIGHT) {
					alignParentLeft(progressPrimaryView)
					alignParentLeft(progressSecondaryView)
					alignParentLeft(markerView)
				}else{
					alignParentRight(progressPrimaryView)
					alignParentRight(progressSecondaryView)
					alignParentRight(markerView)
				}
				markerView?.layoutParams?.width = if(mode==Mode.SEEKBAR) markerHeight.roundToInt() else 0
				markerView?.layoutParams?.height = if(mode==Mode.SEEKBAR) markerWidth.roundToInt() else 0
				clickableView?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				clickableView?.layoutParams?.height = lineWidth.roundToInt()
				backgroundView?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.height = lineWidth.roundToInt()
				progressPrimaryView?.layoutParams?.height   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.height = (lineWidth-(secondaryMargin*2)).toInt()
			}
		}
		updateDimensions2()
	}
	
	private fun updateDimensions2(){
		clickableView?.let {
			when (orientation) {
				TOP_DOWN, DOWN_TOP -> {
					val newPrimary = ((it.height-((primaryMargin+secondaryMargin+generalVerticalMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.height-((secondaryMargin+generalVerticalMargin)*2)) * secondaryPercentage).toInt()
					if (progressPrimaryView?.layoutParams?.height!=newPrimary) progressPrimaryView?.layoutParams?.height = newPrimary
					if (progressSecondaryView?.layoutParams?.height!=newSecondary) progressSecondaryView?.layoutParams?.height = newSecondary
					(newPrimary).let {
						val margin = if(it>0) it else 0
						if(orientation==TOP_DOWN) {
							markerView?.setMargins(top = margin)
						}else{
							markerView?.setMargins(bottom = margin)
						}
					}
				}
				LEFT_RIGHT, RIGHT_LEFT -> {
					val newPrimary = ((it.width-((primaryMargin+secondaryMargin+generalHorizontalMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.width-((secondaryMargin+generalHorizontalMargin)*2)) * secondaryPercentage).toInt()
					if (progressPrimaryView?.layoutParams?.width!=newPrimary) progressPrimaryView?.layoutParams?.width = newPrimary
					if (progressSecondaryView?.layoutParams?.width!=newSecondary) progressSecondaryView?.layoutParams?.width = newSecondary
					(newPrimary).let {
						val margin = if(it>0) it else 0
						if(orientation==LEFT_RIGHT) {
							markerView?.setMargins(left = margin)
						}else{
							markerView?.setMargins(right = margin)
						}
					}
				}
			}
			when(orientation){
				TOP_DOWN -> {
					progressPrimaryView?.setMargins(top = (primaryMargin+secondaryMargin+generalVerticalMargin).toInt())
					progressSecondaryView?.setMargins(top = (secondaryMargin+generalVerticalMargin).toInt())
				}
				DOWN_TOP -> {
					progressPrimaryView?.setMargins(bottom = (primaryMargin+secondaryMargin+generalVerticalMargin).toInt())
					progressSecondaryView?.setMargins(bottom = (secondaryMargin+generalVerticalMargin).toInt())
				}
				LEFT_RIGHT -> {
					progressPrimaryView?.setMargins(left = (primaryMargin+secondaryMargin+generalHorizontalMargin).toInt())
					progressSecondaryView?.setMargins(left = (secondaryMargin+generalHorizontalMargin).toInt())
				}
				RIGHT_LEFT -> {
					progressPrimaryView?.setMargins(right = (primaryMargin+secondaryMargin+generalHorizontalMargin).toInt())
					progressSecondaryView?.setMargins(right = (secondaryMargin+generalHorizontalMargin).toInt())
				}
			}
			//clickableView?.setMargins(top = generalVerticalMargin.toInt(), bottom = generalVerticalMargin.toInt(), left = generalHorizontalMargin.toInt(), right = generalHorizontalMargin.toInt())
			clickableView?.requestLayout()
			backgroundView?.setMargins(top = generalVerticalMargin.toInt(), bottom = generalVerticalMargin.toInt(), left = generalHorizontalMargin.toInt(), right = generalHorizontalMargin.toInt())
			backgroundView?.requestLayout()
			progressPrimaryView?.requestLayout()
			progressSecondaryView?.requestLayout()
		}
	}
	
	fun updateColors() {
		updateColors(backgroundView, backgroundOrientation, backgroundColors, backgroundCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressPrimaryView, primaryOrientation, primaryColors, primaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressSecondaryView, secondaryOrientation, secondaryColors, secondaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(markerView, markerOrientation, markerColors, markerCornerRadii ?: generalCornerRadii ?: mutableListOf())
	}
	
	private fun updateColors(view: View?, orientation: GradientDrawable.Orientation, colorList: List<Int>, customCornerRadii: List<Float>) {
		val colors = colorList.toMutableList()
		if(colors.size==1) colors.add(colors[0])
		view?.apply {
			when {
				colors.size > 1 -> {
					this.setBackgroundCompat(GradientDrawable(orientation, colors.toIntArray()).apply {
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
					})
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
	
	private fun View.setBackgroundCompat(drawable: Drawable){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			this.background = drawable
		}else{
			this.setBackgroundDrawable(drawable)
		}
	}
	
	private fun centerHorizontal(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0)
			}
		}
	}
	
	private fun centerVertical(view: View?){
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0)
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
			}
		}
	}
	
	private fun alignParentTop(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
			}
		}
	}
	
	private fun alignParentBottom(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
			}
		}
	}
	
	private fun alignParentLeft(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
			}
		}
	}
	
	private fun alignParentRight(view: View?) {
		view?.layoutParams?.let { layoutParams ->
			if(layoutParams is RelativeLayout.LayoutParams){
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
			}
		}
	}
	
	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private fun attemptClaimDrag() {
		parent?.requestDisallowInterceptTouchEvent(true)
	}
	
}

enum class Mode {
	PROGRESS,
	SEEKBAR
}

enum class Orientation {
	TOP_DOWN,
	DOWN_TOP,
	LEFT_RIGHT,
	RIGHT_LEFT
}