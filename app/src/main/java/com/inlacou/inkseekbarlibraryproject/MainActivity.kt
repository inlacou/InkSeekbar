package com.inlacou.inkseekbarlibraryproject

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inlacou.animations.easetypes.EaseType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
	
	@SuppressLint("CheckResult")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		inkseekbar_top_down?.backgroundColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.basic_black))
			add(resources.getColorCompat(R.color.basic_grey))
		}
		inkseekbar_top_down?.primaryColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.colorPrimary))
		}
		inkseekbar_top_down?.secondaryColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.colorPrimaryDark))
		}
		inkseekbar_top_down?.generalCornerRadii = listOf(32f)
		inkseekbar_top_down?.primaryMargin = 10f
		inkseekbar_top_down?.secondaryMargin = 15f
		inkseekbar_top_down?.updateBackground()
		val maxProgress = 100
		inkseekbar_top_down?.maxProgress = maxProgress
		inkseekbar_down_top?.maxProgress = maxProgress
		inkseekbar_left_right?.maxProgress = maxProgress
		inkseekbar_right_left?.maxProgress = maxProgress
		inkseekbar_down_top?.generalEaseType = EaseType.EaseOutCubic.newInstance()
		
		inkseekbar_left_right?.onValuePrimarySetListener = { primary, fromUser ->
			if(fromUser) Toast.makeText(this, "primary: $primary", Toast.LENGTH_SHORT).show()
		}
		
		btn?.setOnClickListener {
			restart()
		}
		
		restart()
	}
	
	fun restart() {
		inkseekbar_top_down?.setProgress(0, 0, false)
		inkseekbar_down_top?.setProgress(0, 0, false)
		inkseekbar_right_left?.setProgress(0, 0, false)
		
		//Old (and still valid) method for "animations"
		Observable.interval(50, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({
			inkseekbar_top_down?.let {
				it.setPrimaryProgress(it.primaryProgress+1, fromUser = false, animate = false)
				it.setSecondaryProgress(it.secondaryProgress+2, fromUser = false, animate = false)
			}
		},{ Log.e("MainActObs", "${it.message}") })
		
		//New method for "animations"
		inkseekbar_down_top.setPrimaryProgress(100, fromUser = false, animate = true, duration = 3000L)
		inkseekbar_right_left?.setProgress(100, 100, fromUser = false, animate = true, duration = 2000, durationSecondary = 2000, primaryDelay = 2000)
		
	}
	
	private fun Resources.getColorCompat(resId: Int): Int {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getColor(resId, null)
		}else{
			getColor(resId)
		}
	}
	
}
