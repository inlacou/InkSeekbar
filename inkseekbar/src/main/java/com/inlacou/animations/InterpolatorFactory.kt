package com.inlacou.animations

import android.view.animation.Interpolator

import com.inlacou.animations.easetypes.EaseType

/**
 * Created by Weiping on 2016/3/17.
 */
object InterpolatorFactory {

	fun getInterpolator(easeType: EaseType): BLVInterpolator {
		return BLVInterpolator(easeType)
	}

	class BLVInterpolator(private val easeType: EaseType) : Interpolator {
		override fun getInterpolation(input: Float): Float {
			//Log.d("getInterpolation", "input: $input -> ${easeType.getOffset(input)}")
			return easeType.getOffset(input)
		}
	}
	
}