package com.doaamosallam.trendysteps.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.doaamosallam.trendysteps.BasicActivity
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BasicActivity<ActivityMainBinding>() {
    override fun getLayoutResId() = R.layout.activity_main

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }


}