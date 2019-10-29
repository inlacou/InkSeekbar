package com.inlacou.inkseekbarlibraryproject

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import com.inlacou.inkseekbar.InkSeekbar
import com.inlacou.inkseekbar.Orientation
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
		inkseekbar_top_down?.updateColors()
		val maxProgress = 100
		inkseekbar_top_down?.maxProgress = maxProgress
		inkseekbar_down_top?.maxProgress = maxProgress
		inkseekbar_left_right?.maxProgress = maxProgress
		inkseekbar_right_left?.maxProgress = maxProgress
		Observable.interval(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({
			inkseekbar_top_down?.let {
				it.primaryProgress += 1
				it.secondaryProgress += 2
			}
			inkseekbar_down_top?.let {
				it.primaryProgress += 1
				it.secondaryProgress += 2
			}
			inkseekbar_right_left?.let {
				it.primaryProgress += 1
				it.secondaryProgress += 2
			}
		},{
			Log.d("MainActObs", "${it.message}")
		})
	}
	
	
	private fun Resources.getColorCompat(resId: Int): Int {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getColor(resId, null)
		}else{
			getColor(resId)
		}
	}
	
}
