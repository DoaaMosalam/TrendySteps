package com.doaamosallam.trendysteps.ui.auth.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
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
import com.doaamosallam.trendysteps.utils.Credential
import com.doaamosallam.trendysteps.utils.LoginException
import com.doaamosallam.trendysteps.utils.isEmailValid
import com.doaamosallam.trendysteps.utils.isPasswordValid
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class LoginFragment : BasicFragment<FragmentLoginBinding>(), TextWatcher {
    override fun getLayoutResId(): Int = R.layout.fragment_login

    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

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
    }

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
                            Toast.makeText(
                                requireContext(), "Login successfully", Toast.LENGTH_SHORT
                            ).show()
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
//        val credential = GoogleAuthProvider.getCredential(idToken,null)
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener {task->
//                if (task.isSuccessful){
//                    Log.d(TAG,"signInWithCredential:Success")
//                    val email = task.result.user?.uid
//                    Log.d(TAG,"firebaseAuthWithGoogle:$email")
//                }else{
//                    Log.d(TAG,"signInWithCredential:failure",task.exception)
//                }
//            }
    }



    private fun initLogin(){
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