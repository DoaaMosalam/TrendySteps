package com.doaamosallam.trendysteps.auth

import android.os.Bundle
import android.view.View
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BasicFragment<FragmentLoginBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun getLayoutResId() = R.layout.fragment_login


}