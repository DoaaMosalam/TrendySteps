package com.doaamosallam.trendysteps.ui.auth.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentRegisterBinding
import com.doaamosallam.trendysteps.utils.isConfirmPasswordValid
import com.doaamosallam.trendysteps.utils.isEmailValid
import com.doaamosallam.trendysteps.utils.isNameValid
import com.doaamosallam.trendysteps.utils.isPasswordValid

class RegisterFragment : BasicFragment<FragmentRegisterBinding>(),TextWatcher {
    override fun getLayoutResId(): Int = R.layout.fragment_register

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFocusListener()
        addTextWatcher()
    }
    companion object {
        private const val TAG = "RegisterFragment"
    }

    private fun addTextWatcher() {
        binding.edNameRegister.addTextChangedListener(this)
        binding.edEmailRegister.addTextChangedListener(this)
        binding.edPasswordRegister.addTextChangedListener(this)
        binding.edRePasswordRegister.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
       binding.btnRegister.isEnabled =
           binding.edNameRegister.text.toString().isNameValid() &&
              binding.edEmailRegister.text.toString().isEmailValid() &&
                binding.edPasswordRegister.text.toString().isPasswordValid() &&
                    binding.edRePasswordRegister.text.toString().isConfirmPasswordValid(binding.edPasswordRegister.text.toString())
                   && validateName()
                      && validateEmail()
                            && validatePassword()
                                && validateConfirmPassword()
    }

    private fun validateEmail():Boolean{
        val email = binding.edEmailRegister.text.toString()
        if (!email.isEmailValid()) {
            binding.emailTilRegister.error = "Invalid email address"
            binding.emailTilRegister.endIconDrawable = null
        } else {
            binding.emailTilRegister.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.emailTilRegister.error == null

    }
    private fun validatePassword():Boolean{
        val password = binding.edPasswordRegister.text.toString()
        if (!password.isPasswordValid()) {
            binding.passwordTilRegister.error = "oops! Your Password is Not Correct"
            binding.passwordTilRegister.endIconDrawable = null
        } else {
            binding.passwordTilRegister.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.passwordTilRegister.error == null
    }
    private fun validateName():Boolean{
        val name = binding.edNameRegister.text.toString()
      if (!name.isNameValid()) {
            binding.nameTilRegister.error = "Name is not valid"
            binding.nameTilRegister.endIconDrawable = null
        } else {
            binding.nameTilRegister.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.nameTilRegister.error == null
    }
    private fun validateConfirmPassword():Boolean{
        val password = binding.edPasswordRegister.text.toString()
        val confirmPassword = binding.edRePasswordRegister.text.toString()
       if (!confirmPassword.isConfirmPasswordValid(password)) {
            binding.repasswordTilRegister.error = "Password does not match"
            binding.repasswordTilRegister.endIconDrawable = null
        } else {
            binding.repasswordTilRegister.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.repasswordTilRegister.error == null
    }
    private fun addFocusListener(){
        setNameFocusListener()
        setEmailFocusListener()
        setPasswordFocusListener()
        setConfirmPasswordFocusListener()
    }
    private fun setNameFocusListener(){
        val nameValue = binding.edNameRegister
        nameValue.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                validateName()
            }
        }
    }

    private fun setEmailFocusListener(){
        val emailValue = binding.edEmailRegister
        emailValue.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }
    private fun setPasswordFocusListener(){
        val passwordValue = binding.edPasswordRegister
        passwordValue.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
    }
    private fun setConfirmPasswordFocusListener(){
        val confirmPasswordValue = binding.edRePasswordRegister
        confirmPasswordValue.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            }
        }
    }
}