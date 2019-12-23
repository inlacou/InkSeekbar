package com.inlacou.inkseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.content.res.getDrawableOrThrow
import com.inlacou.animations.Interpolable
import com.inlacou.animations.easetypes.EaseType
import com.inlacou.inkseekbar.Orientation.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

class InkSeekbar: FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) { readAttrs(attrSet) }
	constructor(context: Context, attrSet: AttributeSet, arg: Int) : super(context, attrSet, arg) { readAttrs(attrSet) }
	
	private lateinit var listener: ViewTreeObserver.OnGlobalLayoutListener
	private var disposable: Disposable? = null
	private var clickableView: View? = null
	private var backgroundView: View? = null
	private var progressPrimaryView: View? = null
	private var progressSecondaryView: View? = null
	private var markerView: View? = null
	
	/**
	 * You can create your own Interpolable or use one of my own.
	 * My own:
	 * EaseType.EaseOutBounce.newInstance()
	 * or
	 * EaseType.EaseOutCubic.newInstance()
	 */
	var generalEaseType: Interpolable = EaseType.EaseOutBounce.newInstance()
	/**
	 * You can create your own Interpolable or use one of my own.
	 * My own:
	 * EaseType.EaseOutBounce.newInstance()
	 * or
	 * EaseType.EaseOutCubic.newInstance()
	 */
	var primaryEaseType: Interpolable? = null
	/**
	 * You can create your own Interpolable or use one of my own.
	 * My own:
	 * EaseType.EaseOutBounce.newInstance()
	 * or
	 * EaseType.EaseOutCubic.newInstance()
	 */
	var secondaryEaseType: Interpolable? = null
	
	var lineWidth = 100f
		set(value) {
			field = value
			startUpdate()
		}
	var markerWidth = 100f
		set(value) {
			field = value
			startUpdate()
		}
	var markerHeight = 100f
		set(value) {
			field = value
			startUpdate()
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
	
	private var primaryProgressVisual = 0f
	private var secondaryProgressVisual = 0f
	var primaryProgress = 0
		private set
	var secondaryProgress = 0
		private set
	var maxProgress = 300
		set(value) {
			field = value
			startUpdate()
		}
	var orientation = LEFT_RIGHT
	val primaryPercentageVisual get() =  primaryProgressVisual.toDouble()/maxProgress
	val secondaryPercentageVisual get() = secondaryProgressVisual.toDouble()/maxProgress
	val primaryPercentage get() =  primaryProgress.toDouble()/maxProgress
	val secondaryPercentage get() = secondaryProgress.toDouble()/maxProgress
	
	var generalCornerRadii: List<Float>? = null
	val backgroundColors: MutableList<Int> = mutableListOf()
	var backgroundOrientation: GradientDrawable.Orientation = orientation.toGradientOrientation()
	var backgroundCornerRadii: List<Float>? = null
	
	val primaryColors: MutableList<Int> = mutableListOf()
	var primaryOrientation: GradientDrawable.Orientation = orientation.toGradientOrientation()
	var primaryCornerRadii: List<Float>? = null
	var primaryMargin: Float = 0f
	val secondaryColors: MutableList<Int> = mutableListOf()
	var secondaryOrientation: GradientDrawable.Orientation = orientation.toGradientOrientation()
	var secondaryCornerRadii: List<Float>? = null
	var secondaryMargin: Float = 0f
	var markerIcon: Drawable? = null
	val markerColors: MutableList<Int> = mutableListOf()
	var markerOrientation: GradientDrawable.Orientation = orientation.toGradientOrientation()
	var markerCornerRadii: List<Float>? = null
	var mode: Mode = Mode.PROGRESS
	
	var clickableMode: ClickableMode = ClickableMode.MAX_MARKER_OR_LINE
	
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
				backgroundOrientation = orientation.toGradientOrientation()
				secondaryOrientation = orientation.toGradientOrientation()
				primaryOrientation = orientation.toGradientOrientation()
				markerOrientation = orientation.toGradientOrientation()
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
			if (ta.hasValue(R.styleable.InkSeekbar_markerIcon)) {
				try{
					markerIcon = ta.getDrawableOrThrow(R.styleable.InkSeekbar_markerIcon)
				}catch (e: Exception){}
				
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
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryColors)) {
				ta.resources.getIntArray(ta.getResourceId(R.styleable.InkSeekbar_secondaryColors, -1)).toList().let {
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
				ta.getInt(R.styleable.InkSeekbar_backgroundGradientOrientation, -1).let {
					backgroundOrientation = if(it==-1) orientation.toGradientOrientation()
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_primaryGradientOrientation)) {
				ta.getInt(R.styleable.InkSeekbar_primaryGradientOrientation, -1).let {
					primaryOrientation = if(it==-1) orientation.toGradientOrientation()
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_secondaryGradientOrientation)) {
				ta.getInt(R.styleable.InkSeekbar_secondaryGradientOrientation, -1).let {
					secondaryOrientation = if(it==-1) orientation.toGradientOrientation()
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_markerGradientOrientation)) {
				ta.getInt(R.styleable.InkSeekbar_markerGradientOrientation, -1).let {
					markerOrientation = if(it==-1) orientation.toGradientOrientation()
					else GradientDrawable.Orientation.values()[it]
				}
			}
			if (ta.hasValue(R.styleable.InkSeekbar_clickableMode)) {
				clickableMode = ClickableMode.values()[ta.getInt(R.styleable.InkSeekbar_clickableMode, 0)]
			}
			if (ta.hasValue(R.styleable.InkSeekbar_mode)) {
				mode = Mode.values()[ta.getInt(R.styleable.InkSeekbar_mode, 0)]
			}
		} finally {
			ta.recycle()
		}
		startUpdate()
		updateBackground()
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
		clickableView = rootView.findViewById(R.id.inkseekbar_clickable)
		backgroundView = rootView.findViewById(R.id.inkseekbar_background)
		progressPrimaryView = rootView.findViewById(R.id.inkseekbar_progress_primary)
		progressSecondaryView = rootView.findViewById(R.id.inkseekbar_progress_secondary)
		markerView = rootView.findViewById(R.id.inkseekbar_marker)
		backgroundView?.let {
			it.onDrawn(false) {
				updateSpecific()
			}
		}
		setListeners()
		update()
	}
	
	
	private fun pipo() {
	
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private fun setListeners() {
		listener = ViewTreeObserver.OnGlobalLayoutListener { update()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				viewTreeObserver?.removeOnGlobalLayoutListener(listener)
			}
		}
		viewTreeObserver?.addOnGlobalLayoutListener(listener)
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
			startUpdate()
			
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
	
	//TODO durations here are a bit odd
	fun setPrimaryProgress(value: Int, fromUser: Boolean, animate: Boolean = false, duration: Long = DEFAULT_ANIMATION_DURATION, delay: Long = 0) {
		if(value>maxProgress)
			primaryProgress = maxProgress
		else {
			primaryProgress = value
			if(fromUser) onValueChangeListener?.invoke(value, secondaryProgress)
			onValuePrimaryChangeListener?.invoke(value, fromUser)
			onValuePrimarySetListener?.invoke(value, fromUser)
		}
		startUpdate(animate, durationPrimary = duration, primaryDelay = delay)
	}
	
	//TODO durations here are a bit odd
	fun setSecondaryProgress(value: Int, fromUser: Boolean, animate: Boolean = false, duration: Long = DEFAULT_ANIMATION_DURATION, delay: Long = 0) {
		if(value>maxProgress)
			secondaryProgress = maxProgress
		else {
			secondaryProgress = value
			if(fromUser) onValueChangeListener?.invoke(primaryProgress, value)
			onValueSecondaryChangeListener?.invoke(value, fromUser)
			onValueSecondarySetListener?.invoke(value, fromUser)
		}
		startUpdate(animate, durationSecondary = duration, secondaryDelay = delay)
	}
	
	fun setProgress(primary: Int, secondary: Int, fromUser: Boolean, animate: Boolean = false, duration: Long = DEFAULT_ANIMATION_DURATION, durationSecondary: Long = duration, primaryDelay: Long = 0, secondaryDelay: Long = 0) {
		if(primary>maxProgress) primaryProgress = maxProgress
		if(secondary>maxProgress) secondaryProgress = maxProgress
		if(primary<=maxProgress && secondary<=maxProgress) {
			primaryProgress = primary
			secondaryProgress = secondary
			if(fromUser) {
				onValueChangeListener?.invoke(primaryProgress, secondaryProgress)
			}
			onValuePrimaryChangeListener?.invoke(primary, fromUser)
			onValuePrimarySetListener?.invoke(primary, fromUser)
			onValueSecondaryChangeListener?.invoke(secondary, fromUser)
			onValueSecondarySetListener?.invoke(secondary, fromUser)
		}
		startUpdate(animate, durationPrimary = duration, durationSecondary = durationSecondary, primaryDelay = primaryDelay, secondaryDelay = secondaryDelay)
	}
	
	private fun startUpdate(animate: Boolean = false, durationPrimary: Long = DEFAULT_ANIMATION_DURATION, durationSecondary: Long = DEFAULT_ANIMATION_DURATION, primaryDelay: Long = 0, secondaryDelay: Long = 0) {
		if(animate){
			tryUpdateAnimated(durationPrimary, durationSecondary, primaryDelay, secondaryDelay)
		}else{
			makeUpdate()
		}
	}
	
	private fun makeUpdate() {
		primaryProgressVisual = primaryProgress.toFloat()
		secondaryProgressVisual = secondaryProgress.toFloat()
		update()
	}
	
	private fun tryUpdateAnimated(durationPrimary: Long, durationSecondary: Long, primaryDelay: Long, secondaryDelay: Long) {
		try {
			disposable?.dispose()
			disposable = Observable.interval(10L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe({
				val currentDuration = it * 10L
				if (currentDuration >= durationPrimary+primaryDelay && currentDuration >= durationSecondary+secondaryDelay) disposable?.dispose()
				if(currentDuration in primaryDelay .. (primaryDelay+durationPrimary)) primaryProgressVisual = primaryProgress * (primaryEaseType ?: generalEaseType).getOffset((it-primaryDelay/10L).toFloat() / (durationPrimary / 10L))
				if(currentDuration in secondaryDelay .. (secondaryDelay+durationSecondary)) secondaryProgressVisual = secondaryProgress * (secondaryEaseType ?: generalEaseType).getOffset((it-secondaryDelay/10L).toFloat() / (durationSecondary / 10L))
				update()
			}, {})
		}catch (e: NoClassDefFoundError){
			//RX not present
			//Fault back to not-animated
			Log.w("InkSeekbar", "update-animated failed, RX library not found. Fault back to non-animated updated")
			makeUpdate()
		}
	}
	
	private fun update() {
		when(orientation) {
			TOP_DOWN, DOWN_TOP -> {
				clickableView?.centerHorizontal()
				backgroundView?.centerHorizontal()
				progressPrimaryView?.centerHorizontal()
				progressSecondaryView?.centerHorizontal()
				markerView?.centerHorizontal()
				if(orientation==TOP_DOWN) {
					progressPrimaryView?.alignParentTop()
					progressSecondaryView?.alignParentTop()
					markerView?.alignParentTop()
				}else{
					progressPrimaryView?.alignParentBottom()
					progressSecondaryView?.alignParentBottom()
					markerView?.alignParentBottom()
				}
				markerView?.layoutParams?.width = if(mode==Mode.SEEKBAR) markerWidth.roundToInt() else 0
				markerView?.layoutParams?.height = if(mode==Mode.SEEKBAR) markerHeight.roundToInt() else 0
				clickableView?.layoutParams?.width  = when(clickableMode) {
					ClickableMode.LINE -> lineWidth.roundToInt()
					ClickableMode.MARKER -> markerWidth.toInt()
					ClickableMode.MAX_MARKER_OR_LINE -> max(lineWidth.roundToInt(), markerWidth.toInt())
					ClickableMode.FULL -> max(max(lineWidth.roundToInt(), markerWidth.toInt()), width)
				}
				clickableView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.width  = lineWidth.roundToInt()
				backgroundView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
				progressPrimaryView?.layoutParams?.width   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.width = (lineWidth-(secondaryMargin*2)).toInt()
			}
				Orientation.LEFT_RIGHT, RIGHT_LEFT -> {
				clickableView?.centerVertical()
				backgroundView?.centerVertical()
				progressPrimaryView?.centerVertical()
				progressSecondaryView?.centerVertical()
				markerView?.centerVertical()
				if(orientation==LEFT_RIGHT) {
					progressPrimaryView?.alignParentLeft()
					progressSecondaryView?.alignParentLeft()
					markerView?.alignParentLeft()
				}else{
					progressPrimaryView?.alignParentRight()
					progressSecondaryView?.alignParentRight()
					markerView?.alignParentRight()
				}
				markerView?.layoutParams?.width = if(mode==Mode.SEEKBAR) markerHeight.roundToInt() else 0
				markerView?.layoutParams?.height = if(mode==Mode.SEEKBAR) markerWidth.roundToInt() else 0
				clickableView?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				clickableView?.layoutParams?.height = when(clickableMode) {
					ClickableMode.LINE -> lineWidth.roundToInt()
					ClickableMode.MARKER -> markerHeight.toInt()
					ClickableMode.MAX_MARKER_OR_LINE -> max(lineWidth.roundToInt(), markerHeight.toInt())
					ClickableMode.FULL -> max(max(lineWidth.roundToInt(), markerHeight.toInt()), height)
				}
				backgroundView?.layoutParams?.width  = ViewGroup.LayoutParams.MATCH_PARENT
				backgroundView?.layoutParams?.height = lineWidth.roundToInt()
				progressPrimaryView?.layoutParams?.height   = (lineWidth-((primaryMargin+secondaryMargin)*2)).toInt()
				progressSecondaryView?.layoutParams?.height = (lineWidth-(secondaryMargin*2)).toInt()
			}
		}
		updateSpecific()
	}
	
	private fun updateSpecific(){
		clickableView?.let {
			when (orientation) {
				TOP_DOWN, DOWN_TOP -> {
					val newPrimaryHeight = ((it.height-((primaryMargin+secondaryMargin+generalVerticalMargin)*2)) * primaryPercentageVisual).toInt()
					val newSecondaryHeight = ((it.height-((secondaryMargin+generalVerticalMargin)*2)) * secondaryPercentageVisual).toInt()
					if (progressPrimaryView?.layoutParams?.height!=newPrimaryHeight) progressPrimaryView?.layoutParams?.height = newPrimaryHeight
					if (progressSecondaryView?.layoutParams?.height!=newSecondaryHeight) progressSecondaryView?.layoutParams?.height = newSecondaryHeight
					(newPrimaryHeight).let {
						//We use the margin to set the marker position.
						val margin = if(it>0) it else 0
						if(orientation==TOP_DOWN) {
							markerView?.setMargins(top = margin)
						}else{
							markerView?.setMargins(bottom = margin)
						}
					}
				}
				LEFT_RIGHT, RIGHT_LEFT -> {
					val newPrimaryWidth = ((it.width-((primaryMargin+secondaryMargin+generalHorizontalMargin)*2)) * primaryPercentageVisual).toInt()
					val newSecondaryWidth = ((it.width-((secondaryMargin+generalHorizontalMargin)*2)) * secondaryPercentageVisual).toInt()
					if (progressPrimaryView?.layoutParams?.width!=newPrimaryWidth) progressPrimaryView?.layoutParams?.width = newPrimaryWidth
					if (progressSecondaryView?.layoutParams?.width!=newSecondaryWidth) progressSecondaryView?.layoutParams?.width = newSecondaryWidth
					(newPrimaryWidth).let {
						//We use the margin to set the marker position.
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
			clickableView?.requestLayout()
			backgroundView?.setMargins(top = generalVerticalMargin.toInt(), bottom = generalVerticalMargin.toInt(), left = generalHorizontalMargin.toInt(), right = generalHorizontalMargin.toInt())
			backgroundView?.requestLayout()
			progressPrimaryView?.requestLayout()
			progressSecondaryView?.requestLayout()
		}
	}
	
	fun updateBackground() {
		updateBackground(backgroundView, backgroundOrientation, sanitizeColors(backgroundView, backgroundColors), backgroundCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateBackground(progressPrimaryView, primaryOrientation, sanitizeColors(progressPrimaryView, primaryColors), primaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateBackground(progressSecondaryView, secondaryOrientation, sanitizeColors(progressSecondaryView, secondaryColors), secondaryCornerRadii ?: generalCornerRadii ?: mutableListOf())
		updateBackground(markerView, markerOrientation, sanitizeColors(markerView, markerColors), markerCornerRadii ?: generalCornerRadii ?: mutableListOf(), markerIcon)
	}
	
	/**
	 * Method to sanitize a colors list array to have the correct number of items.
	 * @param colors Array to sanitize.
	 * @param view View to get the current color from if possible.
	 * @return colors list of 2 or more items. Always.
	 */
	private fun sanitizeColors(view: View?, colors: List<Int>): List<Int> {
		return when {
			colors.size>1  -> colors
			colors.size==1 ->
				colors.toMutableList().apply {
					add(colors.first())
				}
			else ->
				mutableListOf<Int>().apply {
					view?.background.let { back ->
						//Try to get color from background
						if(back!=null && back is ColorDrawable && Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
							add(back.color)
							add(back.color)
						}else{
							//Else get default colors
							when(view?.id) {
								R.id.inkseekbar_background -> R.color.inkseekbar_background_default
								R.id.inkseekbar_marker -> R.color.inkseekbar_marker_default
								R.id.inkseekbar_progress_primary -> R.color.inkseekbar_primary_default
								R.id.inkseekbar_progress_secondary -> R.color.inkseekbar_secondary_default
								else -> R.color.inkseekbar_default_default
							}.let {
								add(it)
								add(it)
							}
						}
					}
				}
		}
	}
	
	/**
	 * Changes the background of the view to a GradientDrawable with the given params.
	 * @param colorList colors for the GradientDrawable (always, minimum of 2 items)
	 * @param orientation GradientDrawable.Orientation
	 * @param customCornerRadii GradientDrawable corner radii. 1, 4 or 8 items, else ignored.
	 * @param view View to apply the background to
	 */
	private fun updateBackground(view: View?, orientation: GradientDrawable.Orientation, colorList: List<Int>, customCornerRadii: List<Float>, drawable: Drawable? = null) {
		view?.apply {
			this.setBackgroundCompat(drawable ?: GradientDrawable(orientation, colorList.toIntArray()).apply {
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
	}
	
	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private fun attemptClaimDrag() {
		parent?.requestDisallowInterceptTouchEvent(true)
	}
	
	companion object {
		const val DEFAULT_ANIMATION_DURATION = 2_000L
	}
	
}

private fun Orientation.toGradientOrientation(): GradientDrawable.Orientation {
	Log.d("toGradientOrientation", "orientation: ${this.name}")
	return when(this){
		TOP_DOWN -> GradientDrawable.Orientation.TOP_BOTTOM
		DOWN_TOP -> GradientDrawable.Orientation.BOTTOM_TOP
		LEFT_RIGHT -> GradientDrawable.Orientation.LEFT_RIGHT
		RIGHT_LEFT -> GradientDrawable.Orientation.RIGHT_LEFT
	}
}

enum class Mode {
	PROGRESS,
	SEEKBAR
}

enum class ClickableMode {
	LINE,
	MARKER,
	MAX_MARKER_OR_LINE,
	FULL
}

enum class Orientation {
	TOP_DOWN,
	DOWN_TOP,
	LEFT_RIGHT,
	RIGHT_LEFT
}
