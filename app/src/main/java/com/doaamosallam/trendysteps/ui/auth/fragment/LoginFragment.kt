package com.doaamosallam.trendysteps.ui.auth.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.doaamosallam.trendysteps.BasicFragment
import com.doaamosallam.trendysteps.BuildConfig
import com.doaamosallam.trendysteps.R
import com.doaamosallam.trendysteps.data.local.UserPreferencesDataSource
import com.doaamosallam.trendysteps.data.model.Resource
import com.doaamosallam.trendysteps.data.repository.auth.FirebaseAuthRepositoryImpl
import com.doaamosallam.trendysteps.data.repository.user.UserDataStoreRepositoryImpl
import com.doaamosallam.trendysteps.databinding.FragmentLoginBinding
import com.doaamosallam.trendysteps.ui.auth.viewmodel.LoginViewModel
import com.doaamosallam.trendysteps.ui.auth.viewmodel.LoginViewModelFactory
import com.doaamosallam.trendysteps.ui.common.views.ProgressDialog
import com.doaamosallam.trendysteps.ui.showSnakeBarError
import com.doaamosallam.trendysteps.utils.CrashlyticsUtils
import com.doaamosallam.trendysteps.utils.LoginException
import com.doaamosallam.trendysteps.utils.isEmailValid
import com.doaamosallam.trendysteps.utils.isPasswordValid
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch


class LoginFragment : BasicFragment<FragmentLoginBinding>(), TextWatcher {
    override fun getLayoutResId(): Int = R.layout.fragment_login

    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPrefs = UserDataStoreRepositoryImpl(
                UserPreferencesDataSource(
                    requireActivity()
                )
            ), authRepository = FirebaseAuthRepositoryImpl()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginViewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToRegisterFragment()
        navigateToForgetPasswordFragment()

        addFocusListener()
        addTextWatcher()

        initListeners()

        initViewModel()

    }
    private fun initListeners() {
        binding.btnGoogle.setOnClickListener {
            loginWithGoogleRequest()
        }
        binding.btnFacebook.setOnClickListener {
            loginWithFacebook()
        }
    }

    private fun signOut() {
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    private fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }
//===========================================
    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                state.let { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            progressDialog.show()
                        }

                        is Resource.Success -> {
                            progressDialog.dismiss()
                            navigateToHome()
                        }

                        is Resource.Error -> {
                            progressDialog.dismiss()
                            val msg = resource.exception?.message?:getString(R.string.generic_error_msg)
                            view?.showSnakeBarError(resource.exception?.message ?: getString(R.string.generic_error_msg))
                            logAuthIssueToCrashlytics(msg)
                        }
                    }
                }
            }
        }
    }
//================================================================================
    // handle code login with google.
    private fun loginWithGoogleRequest() {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.clientServerId) // Your Web Client ID from Firebase Console
            .requestEmail()
            .requestProfile()
            .build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), option)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        Log.d(TAG,"onActivityResult: ${result.resultCode}")
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }else{
                view?.showSnakeBarError(getString(R.string.google_sign_in_failed_msg))
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
            navigateToHome()
        } catch (e: Exception) {
            view?.showSnakeBarError(e.message?:getString(R.string.generic_error_msg))
            val msg = e.message?:getString(R.string.generic_error_msg)
           logAuthIssueToCrashlytics(msg)
        }
    }
    private fun logAuthIssueToCrashlytics(msg:String){
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg
        )
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        loginViewModel.loginWithGoogle(idToken)
    }

    //=========================================================================
    // handle code login with facebook.
    private fun loginWithFacebook() {
        if (isLoggedIn()) signOut()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d(TAG, "onSuccess: $token")
                firebaseAuthWithFacebook(token)

            }

            override fun onCancel() {
                // Handle login cancel
            }

            override fun onError(error: FacebookException) {
                // Handle login error
                val msg = error.message ?: getString(R.string.generic_error_msg)
                Log.d(TAG, "onError: $msg")
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg)
            }
        })

        loginManager.logInWithReadPermissions(
            this, callbackManager, listOf("email", "public_profile")
        )
    }

    private fun firebaseAuthWithFacebook(token: String) {
        loginViewModel.loginWithFacebook(token)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
//==========================================================
private fun navigateToHome(){
    binding.btnLogin.setOnClickListener {v->

        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeFragment)
    }
}
    private fun navigateToRegisterFragment() {
        binding.btnCreateCount.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun navigateToForgetPasswordFragment() {
        binding.btnForgetPassword.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
    }
//=================================================================
    private fun addTextWatcher() {
        binding.edEmailLogin.addTextChangedListener(this)
        binding.edPasswordLogin.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        binding.btnLogin.isEnabled =
            binding.edEmailLogin.text!!.trim().toString().isNotEmpty()
                    && binding.edPasswordLogin.text.toString().isNotEmpty()
                    && validateEmail()
                    && validatePassword()
    }

    private fun addFocusListener() {
        setEmailFocusListener()
        setPasswordFocusListener()
    }

    private fun setEmailFocusListener() {
        val emailValue = binding.edEmailLogin
        emailValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }

    private fun setPasswordFocusListener() {
        val passwordValue = binding.edPasswordLogin
        passwordValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.edEmailLogin.text.toString()
        if (!email.isEmailValid()) {
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

    private fun validatePassword(): Boolean {
        val password = binding.edPasswordLogin.text.toString()
        if (!password.isPasswordValid()) {
            binding.passworrdTilLogin.error = "oops! Your Password is Not Correct"
            binding.passworrdTilLogin.endIconDrawable = null
        }
        else {
            binding.passworrdTilLogin.apply {
                error = null
                setStartIconDrawable(R.drawable.baseline_check_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }
        return binding.passworrdTilLogin.error == null
    }

    companion object{
        const val TAG="Login"
    }
}