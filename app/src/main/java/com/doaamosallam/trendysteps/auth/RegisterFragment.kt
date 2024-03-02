package com.doaamosallam.trendysteps.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private lateinit var bindingRegister: FragmentRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingRegister = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        return bindingRegister.root

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {

    }
}