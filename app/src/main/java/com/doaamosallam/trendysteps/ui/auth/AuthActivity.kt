package com.doaamosallam.trendysteps.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.ui.auth.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}