package com.doaamosallam.trendysteps.ui.splashscreen

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.doaamosallam.trendysteps.BasicActivity
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.data.local.UserPreferencesDataSource
import com.doaamosallam.trendysteps.data.repository.user.UserDataStoreRepositoryImpl
import com.doaamosallam.trendysteps.databinding.ActivitySplashScreenBinding
import com.doaamosallam.trendysteps.ui.auth.AuthActivity
import com.doaamosallam.trendysteps.ui.common.viewmodel.UserViewModel
import com.doaamosallam.trendysteps.ui.common.viewmodel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : BasicActivity<ActivitySplashScreenBinding>() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserDataStoreRepositoryImpl(UserPreferencesDataSource(this)))
    }
    override fun getLayoutResId() = R.layout.activity_splash_screen

    override fun onCreate(savedInstanceState: Bundle?) {
        // call method initSplashScreen()
        initSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Main) {
            val isLoggedIn = userViewModel.isUserLoggedIn().first()
            Log.d(TAG, "onCreate: isLoggedIn: $isLoggedIn")
            if (isLoggedIn) {
                setContentView(R.layout.activity_main)
            } else {
                userViewModel.setIsLoggedIn(true)
                goToAuthActivity()
            }
        }

    }
    override fun onResume() {
        super.onResume()
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val options = ActivityOptions.makeCustomAnimation(
            this, android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
    }

    // This method is used to hide the status bar and make the splash screen as a full screen activity.
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
                slideUp.doOnEnd { splashScreenViewProvider.remove() }
                //Run the animation.
                slideUp.start()
            }
        } else {
            setTheme(R.style.Theme_TrendySteps)
        }
    }


}