package com.doaamosallam.trendysteps.ui.auth.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.data.repository.UserDataStoreRepositoryImpl
import com.doaamosallam.trendysteps.databinding.FragmentLoginBinding
import com.doaamosallam.trendysteps.ui.auth.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.AlertDialog
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


@AndroidEntryPoint
class LoginFragment : BasicFragment<FragmentLoginBinding>(), View.OnClickListener,TextWatcher{
//    private lateinit var binding: FragmentLoginBinding
    override fun getLayoutResId() = R.layout.fragment_login

    private lateinit var checkIcon: Drawable

    val loginViewModel: LoginViewModel by lazy {
        LoginViewModel(userPrefs = UserDataStoreRepositoryImpl(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        // Initialize checkIcon
        checkIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_check_24)!!


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add text change listeners to EditTexts
        addTextChangeListeners()

        // Set click listeners to buttons
        setClickListeners()

        // Set focus listeners to EditTexts
        setFocusListeners()

        // Set the toolbar as the support action bar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMainLogin)

        // Set the title of the toolbar
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.login)
    }
    // Add text change listeners to EditTexts
    private fun addTextChangeListeners() {
        binding.edEmailLogin.addTextChangedListener(this)
        binding.edPasswordLogin.addTextChangedListener(this)
    }

    // Set click listeners to buttons
    private fun setClickListeners() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnForgetPassword.setOnClickListener(this)
        binding.btnCreateCount.setOnClickListener(this)
    }

    // Set focus listeners to EditTexts
    private fun setFocusListeners() {
        emailFocusListener()
        passwordFocusListener()
    }

//==============================
    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.btn_login->{

                }
                R.id.btn_forgetPassword->{
                    findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)

                }
                R.id.btn_create_count->{
                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
//==================================================================================================
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        binding.btnLogin.isEnabled =
            binding.edEmailLogin.text!!.trim().toString().isNotEmpty()
                    && binding.edPasswordLogin.text!!.trim().toString().isNotEmpty()
                    &&validateEmail()
                    &&validatePassword()
    }

    //=====================================================================================
    private fun validateEmail(): Boolean {
        val email = binding.edEmailLogin.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailTilLogin.error = "Email is required"
            binding.emailTilLogin.endIconDrawable = null
        } else if (!isValidationEmail(email)) {
            binding.emailTilLogin.error = "Invalid email address"
            binding.emailTilLogin.endIconDrawable = null
        } else {
            binding.emailTilLogin.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.emailTilLogin.error == null
    }

    private fun isValidationEmail(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(pattern.toRegex())

    }

    private fun validatePassword(): Boolean {
        val password = binding.edPasswordLogin.text.toString().trim()

        if (password.isEmpty()) {
            binding.passworrdTilLogin.error = "Password is required"
            binding.passworrdTilLogin.endIconDrawable = null
        } else if (password.length < 8) {
            binding.passworrdTilLogin.error = "Password must be at least 8 characters"
            binding.passworrdTilLogin.endIconDrawable = null
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            binding.passworrdTilLogin.error =
                "Password must contain 1 upper-case character"
            binding.passworrdTilLogin.endIconDrawable = null
        } else if (!password.matches(".*[a-z].*".toRegex())) {
            binding.passworrdTilLogin.error =
                "Password must contain 1 lower-case character"
        } else if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
            binding.passworrdTilLogin.error =
                "Password must contain special[@#\$%^&+=] "
        } else if (!password.matches(".*[1-9]|10.*".toRegex())) {
            binding.passworrdTilLogin.error = "Password must contains numbers 1:10"
        } else {
            binding.passworrdTilLogin.apply {
                error = null
                endIconDrawable = checkIcon
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.passworrdTilLogin.error == null
    }

    private fun emailFocusListener() {
        val emailValue = binding.edEmailLogin
        emailValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }

    private fun passwordFocusListener() {
        val passwordValue = binding.edPasswordLogin
        passwordValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
    }
    companion object {

        private const val TAG = "LoginFragment"
    }
}