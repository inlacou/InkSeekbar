package com.inlacou.inkseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
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
	
	private var backgroundView: View? = null
	private var progressPrimaryView: View? = null
	private var progressSecondaryView: View? = null
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
	
	private fun readAttrs(attrs: AttributeSet) {
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
			if (ta.hasValue(R.styleable.InkSeekbar_backgroundGradientOrientation)) {
				backgroundOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_backgroundGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryGradientOrientation)) {
				primaryOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_primaryGradientOrientation, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryGradientOrientation)) {
				secondaryOrientation = GradientDrawable.Orientation.values()[ta.getInt(R.styleable.InkSeekbar_secondaryGradientOrientation, 0)]
			}
		} finally {
			ta.recycle()
		}
		updateDimensions()
		updateDimensions2()
		updateColors()
	}
	
	private val totalSize: Int get() = when(orientation){
		TOP_DOWN, DOWN_TOP -> {
			backgroundView?.let {
				it.height-((primaryMargin+secondaryMargin)*2).toInt()
			} ?: 0
		}
		LEFT_RIGHT, RIGHT_LEFT ->
			backgroundView?.let {
				it.width-((primaryMargin+secondaryMargin)*2).toInt()
			} ?: 0
	}
	private val stepSize: Int get() = totalSize/maxProgress
	private val reversed: Boolean get() = orientation==DOWN_TOP || orientation==RIGHT_LEFT
	
	init {
		Log.d("InkSeekbar", "init")
		val rootView = View.inflate(context, R.layout.ink_seekbar, this)
		backgroundView = rootView.findViewById(R.id.background)
		progressPrimaryView = rootView.findViewById(R.id.progress_primary)
		progressSecondaryView = rootView.findViewById(R.id.progress_secondary)
		marker = rootView.findViewById(R.id.marker)
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
		backgroundView?.setOnTouchListener { _, event ->
			val relativePosition = when(orientation){
				TOP_DOWN, DOWN_TOP -> event.y
				LEFT_RIGHT, RIGHT_LEFT -> event.x
			} //reaches 0 at top and goes on the minus realm if you keep going up
			val roughStep = if(reversed) (relativePosition/stepSize)-1 else (relativePosition/stepSize)
			val step = (relativePosition/stepSize).roundToInt()
			Log.d("touchListener", "step: $step | roughStep: $roughStep")
			val newPosition = step //TODO calculate new position
			if(primaryProgress!=newPosition) {
				//TODO change and fire listener value change
				//controller.onCurrentItemChanged(newPosition, true)
			}
			primaryProgress = newPosition
			//TODO update updateDisplays(event.action== MotionEvent.ACTION_MOVE || event.action== MotionEvent.ACTION_DOWN)
			
			when(event.action){
				MotionEvent.ACTION_DOWN -> {
					attemptClaimDrag()
					true
				}
				MotionEvent.ACTION_CANCEL -> false
				MotionEvent.ACTION_UP -> {
					//TODO fire listener release controller.onTouchRelease()
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
				centerHorizontal(backgroundView)
				centerHorizontal(progressPrimaryView)
				centerHorizontal(progressSecondaryView)
				if(orientation==TOP_DOWN) {
					alignParentTop(progressPrimaryView)
					alignParentTop(progressSecondaryView)
				}else{
					alignParentBottom(progressPrimaryView)
					alignParentBottom(progressSecondaryView)
				}
				backgroundView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.width  = lineWidth
				progressPrimaryView?.layoutParams?.width   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.width = (lineWidth-(secondaryMargin*2)).toInt()
			}
			LEFT_RIGHT, RIGHT_LEFT -> {
				centerVertical(backgroundView)
				centerVertical(progressPrimaryView)
				centerVertical(progressSecondaryView)
				if(orientation==LEFT_RIGHT) {
					alignParentLeft(progressPrimaryView)
					alignParentLeft(progressSecondaryView)
				}else{
					alignParentRight(progressPrimaryView)
					alignParentRight(progressSecondaryView)
				}
				backgroundView?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.height = lineWidth
				progressPrimaryView?.layoutParams?.height   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.height = (lineWidth-(secondaryMargin*2)).toInt()
			}
		}
		updateDimensions2()
	}
	
	private fun updateDimensions2(){
		backgroundView?.let {
			when (orientation) {
				TOP_DOWN, DOWN_TOP -> {
					val newPrimary = ((it.height-((primaryMargin+secondaryMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.height-(secondaryMargin*2)) * secondaryPercentage).toInt()
					if (progressPrimaryView?.layoutParams?.height != newPrimary) progressPrimaryView?.layoutParams?.height = newPrimary
					if (progressSecondaryView?.layoutParams?.height != newPrimary) progressSecondaryView?.layoutParams?.height = newSecondary
				}
				LEFT_RIGHT, RIGHT_LEFT -> {
					val newPrimary = ((it.width-((primaryMargin+secondaryMargin)*2)) * primaryPercentage).toInt()
					val newSecondary = ((it.width-(secondaryMargin*2)) * secondaryPercentage).toInt()
					if (progressPrimaryView?.layoutParams?.width != newPrimary) progressPrimaryView?.layoutParams?.width = newPrimary
					if (progressSecondaryView?.layoutParams?.width != newPrimary) progressSecondaryView?.layoutParams?.width = newSecondary
				}
			}
			when(orientation){
				TOP_DOWN -> {
					progressPrimaryView?.setMargins(top = (primaryMargin+secondaryMargin).toInt())
					progressSecondaryView?.setMargins(top = (secondaryMargin).toInt())
				}
				DOWN_TOP -> {
					progressPrimaryView?.setMargins(bottom = (primaryMargin+secondaryMargin).toInt())
					progressSecondaryView?.setMargins(bottom = (secondaryMargin).toInt())
				}
				LEFT_RIGHT -> {
					progressPrimaryView?.setMargins(left = (primaryMargin+secondaryMargin).toInt())
					progressSecondaryView?.setMargins(left = (secondaryMargin).toInt())
				}
				RIGHT_LEFT -> {
					progressPrimaryView?.setMargins(right = (primaryMargin+secondaryMargin).toInt())
					progressSecondaryView?.setMargins(right = (secondaryMargin).toInt())
				}
			}
			progressPrimaryView?.requestLayout()
			progressSecondaryView?.requestLayout()
		}
	}
	
	fun updateColors() {
		updateColors(backgroundView, backgroundOrientation, backgroundColors, backgroundCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressPrimaryView, primaryOrientation, primaryColors, primaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateColors(progressSecondaryView, secondaryOrientation, secondaryColors, secondaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
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
	
	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private fun attemptClaimDrag() {
		parent?.requestDisallowInterceptTouchEvent(true)
	}
	
}

enum class Orientation {
	TOP_DOWN,
	DOWN_TOP,
	LEFT_RIGHT,
	/** Not working */ RIGHT_LEFT
}