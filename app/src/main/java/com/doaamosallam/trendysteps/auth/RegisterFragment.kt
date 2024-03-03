package com.doaamosallam.trendysteps.auth

import android.os.Bundle
import android.view.View
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class RegisterFragment : BasicFragment<FragmentRegisterBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {

    }

    override fun getLayoutResId() = R.layout.fragment_register
}