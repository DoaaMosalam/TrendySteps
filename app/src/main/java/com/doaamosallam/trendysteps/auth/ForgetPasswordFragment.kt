package com.doaamosallam.trendysteps.auth

import android.os.Bundle
import android.view.View
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentForgetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : BasicFragment<FragmentForgetPasswordBinding>() {
    override fun getLayoutResId() = R.layout.fragment_forget_password
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    companion object {

    }

}