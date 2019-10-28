package com.inlacou.inkseekbarlibraryproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
		Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({
			Log.d("MainAct", "step")
			inkSeekbar?.let {
				it.maxProgress += 1
				it.primaryProgress += 3
				it.secondaryProgress += 6
				it.layoutParams.width += 10
			}
		},{
			Log.d("MainAct", "${it.message}")
		})
	}
}
