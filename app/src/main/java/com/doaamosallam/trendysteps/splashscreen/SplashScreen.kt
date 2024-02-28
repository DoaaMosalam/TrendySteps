package com.doaamosallam.trendysteps.splashscreen

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieAnimationView
import com.doaamosallam.trendysteps.MainActivity
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.auth.LoginFragment
import com.doaamosallam.trendysteps.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    private lateinit var bindingSplash: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        bindingSplash = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

    }
    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenViewProvider.view,
                    "translationY",
                    0f,
                    -splashScreenViewProvider.view.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 1000L
                slideUp.doOnEnd { splashScreenViewProvider.remove()
                    navigateToLoginFragment()
                }
                slideUp.start()
            }
        } else {
            setTheme(R.style.Theme_TrendySteps)
        }
    }

    private fun navigateToLoginFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigate_to_login_fragment", true)
        startActivity(intent)
        finish()
    }
}