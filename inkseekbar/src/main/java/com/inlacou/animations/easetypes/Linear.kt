package com.inlacou.animations.easetypes

/**
 * Created by Weiping on 2016/3/3.
 */

class Linear : CubicBezier() {
	init {
		init(0f, 0f, 1f, 1f)
	}

	override fun getOffset(offset: Float): Float = offset
}
