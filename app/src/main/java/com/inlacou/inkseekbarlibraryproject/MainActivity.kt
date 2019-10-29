package com.inlacou.inkseekbarlibraryproject

import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import com.inlacou.inkseekbar.InkSeekbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
	
	var inkSeekbar: InkSeekbar? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		inkSeekbar = findViewById(R.id.inkseekbar)
		inkSeekbar?.backgroundColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.basic_black))
			add(resources.getColorCompat(R.color.basic_grey))
		}
		inkSeekbar?.primaryColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.colorPrimary))
		}
		inkSeekbar?.secondaryColors?.apply {
			clear()
			add(resources.getColorCompat(R.color.colorPrimaryDark))
		}
		inkSeekbar?.generalCornerRadii = listOf(32f)
		inkSeekbar?.updateColors()
		inkSeekbar?.maxProgress = 10000
		Observable.interval(10, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({
			Log.d("MainAct", "step")
			inkSeekbar?.let {
				it.primaryProgress += 1
				it.secondaryProgress += 2
			}
		},{
			Log.d("MainAct", "${it.message}")
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
