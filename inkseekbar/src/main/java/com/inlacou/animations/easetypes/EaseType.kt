package com.inlacou.animations.easetypes

import com.inlacou.animations.Interpolable

/**
 * Created by Weiping on 03/03/2016.
 * Updated by Inlacou on 27/11/2019.
 * Visual representation here: https://easings.net/en
 */
enum class EaseType

/**
 * ease animation helps to make the movement more real
 * @param easingType
 */
constructor(private val easingType: Class<out Interpolable>) {
	None(NoEase::class.java),

	EaseInSine(com.inlacou.animations.easetypes.EaseInSine::class.java),
	EaseOutSine(com.inlacou.animations.easetypes.EaseOutSine::class.java),
	EaseInOutSine(com.inlacou.animations.easetypes.EaseInOutSine::class.java),

	EaseInQuad(com.inlacou.animations.easetypes.EaseInQuad::class.java),
	EaseOutQuad(com.inlacou.animations.easetypes.EaseOutQuad::class.java),
	EaseInOutQuad(com.inlacou.animations.easetypes.EaseInOutQuad::class.java),

	EaseInCubic(com.inlacou.animations.easetypes.EaseInCubic::class.java),
	EaseOutCubic(com.inlacou.animations.easetypes.EaseOutCubic::class.java),
	EaseInOutCubic(com.inlacou.animations.easetypes.EaseInOutCubic::class.java),

	EaseInQuart(com.inlacou.animations.easetypes.EaseInQuart::class.java),
	EaseOutQuart(com.inlacou.animations.easetypes.EaseOutQuart::class.java),
	EaseInOutQuart(com.inlacou.animations.easetypes.EaseInOutQuart::class.java),

	EaseInQuint(com.inlacou.animations.easetypes.EaseInQuint::class.java),
	EaseOutQuint(com.inlacou.animations.easetypes.EaseOutQuint::class.java),
	EaseInOutQuint(com.inlacou.animations.easetypes.EaseInOutQuint::class.java),

	EaseInExpo(com.inlacou.animations.easetypes.EaseInExpo::class.java),
	EaseOutExpo(com.inlacou.animations.easetypes.EaseOutExpo::class.java),
	EaseInOutExpo(com.inlacou.animations.easetypes.EaseInOutExpo::class.java),

	EaseInCirc(com.inlacou.animations.easetypes.EaseInCirc::class.java),
	EaseOutCirc(com.inlacou.animations.easetypes.EaseOutCirc::class.java),
	EaseInOutCirc(com.inlacou.animations.easetypes.EaseInOutCirc::class.java),

	EaseInBack(com.inlacou.animations.easetypes.EaseInBack::class.java),
	EaseOutBack(com.inlacou.animations.easetypes.EaseOutBack::class.java),
	EaseInOutBack(com.inlacou.animations.easetypes.EaseInOutBack::class.java),

	EaseInElastic(com.inlacou.animations.easetypes.EaseInElastic::class.java),
	EaseOutElastic(com.inlacou.animations.easetypes.EaseOutElastic::class.java),
	EaseInOutElastic(com.inlacou.animations.easetypes.EaseInOutElastic::class.java),

	EaseInBounce(com.inlacou.animations.easetypes.EaseInBounce::class.java),
	EaseOutBounce(com.inlacou.animations.easetypes.EaseOutBounce::class.java),
	EaseInOutBounce(com.inlacou.animations.easetypes.EaseInOutBounce::class.java),

	Linear(com.inlacou.animations.easetypes.Linear::class.java);
	
	fun newInstance() = easingType.newInstance()
}
