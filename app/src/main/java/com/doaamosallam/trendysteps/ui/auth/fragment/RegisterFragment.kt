package com.doaamosallam.trendysteps.ui.auth.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class RegisterFragment : BasicFragment<FragmentRegisterBinding>(),View.OnClickListener,TextWatcher {
    override fun getLayoutResId() = R.layout.fragment_register
    private lateinit var checkIcon: Drawable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_check_24)!!
        addTextChangeListeners()
        setClickListeners()
        setFocusListeners()

    }

    companion object {

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_register -> {

                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        binding.btnRegister.isEnabled =
            binding.edNameRegister.text.toString().isNotEmpty() &&
            binding.edEmailRegister.text.toString().isNotEmpty() &&
            binding.edPasswordRegister.text.toString().isNotEmpty() &&
            binding.edRePasswordRegister.text.toString().isNotEmpty() &&
            validateEmail() && validatePassword()
    }
    private fun addTextChangeListeners() {
        binding.edNameRegister.addTextChangedListener(this)
        binding.edEmailRegister.addTextChangedListener(this)
        binding.edPasswordRegister.addTextChangedListener(this)
        binding.edRePasswordRegister.addTextChangedListener  (this)
    }
    private fun setClickListeners() {
        binding.btnRegister.setOnClickListener(this)
    }
    private fun setFocusListeners() {
        nameFocusListener()
        emailFocusListener()
        passwordFocusListener()
        returnPasswordListener()
    }
    private fun validateFullName() {
        val name = binding.edNameRegister.text.toString().trim()
        if (name.isEmpty()){
            binding.nameTilRegister.error = "Name is required"
            binding.nameTilRegister.endIconDrawable = null
        }else{
            binding.nameTilRegister.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }

    }
    private fun validateEmail(): Boolean {
        val email = binding.edEmailRegister.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailTilRegister.error = "Email is required"
            binding.emailTilRegister.endIconDrawable = null
        } else if (!isValidationEmail(email)) {
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
    private fun isValidationEmail(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(pattern.toRegex())

    }

    private fun validatePassword(): Boolean {
        val password = binding.edRePasswordRegister.text.toString().trim()

        if (password.isEmpty()) {
            binding.passwordTilRegister.error = "Password is required"
            binding.passwordTilRegister.endIconDrawable = null
        } else if (password.length < 8) {
            binding.passwordTilRegister.error = "Password must be at least 8 characters"
            binding.passwordTilRegister.endIconDrawable = null
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            binding.passwordTilRegister.error =
                "Password must contain 1 upper-case character"
            binding.passwordTilRegister.endIconDrawable = null
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            binding.passwordTilRegister.error =
                "Password must contain 1 lower-case character"
        } else if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
            binding.passwordTilRegister.error =
                "Password must contain special[@#\$%^&+=] "
        } else if (!password.matches(".*[1-9]|10.*".toRegex())) {
            binding.passwordTilRegister.error = "Password must contains numbers 1:10"
        } else {
            binding.passwordTilRegister.apply {
                error = null
                endIconDrawable = checkIcon
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.passwordTilRegister.error == null
    }
    private fun returnPassword(){
        val password = binding.edPasswordRegister.text.toString().trim()
        val rePassword = binding.edRePasswordRegister.text.toString().trim()
        if (password != rePassword){
            binding.passwordTilRegister.error = "Password not match"
            binding.passwordTilRegister.endIconDrawable = null
        }else{
            binding.passwordTilRegister.apply {
                error = null
                endIconDrawable = checkIcon
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
    }
    private fun nameFocusListener() {
        val fullNameValue = binding.edNameRegister
        fullNameValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateFullName()
            }
        }
    }

    private fun emailFocusListener() {
        val emailValue = binding.edEmailRegister
        emailValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }

    private fun passwordFocusListener() {
        val passwordValue = binding.edPasswordRegister
        passwordValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
    }
    private fun returnPasswordListener() {
        val passwordValue = binding.edRePasswordRegister
        passwordValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                returnPassword()
            }
        }
    }
}