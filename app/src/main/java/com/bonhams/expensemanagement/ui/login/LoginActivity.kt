package com.bonhams.expensemanagement.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.databinding.ActivityLoginBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class LoginActivity : BaseActivity() {
    private lateinit var viewModel: LoginViewModel
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
//        setContentView(R.layout.activity_login)

        setupViewModel()
        setClickListeners()
        setNoInternetSnackbar()
    }

    private fun setClickListeners(){
        binding.mContinue.setOnClickListener {
            if(NoInternetUtils.isConnectedToInternet(this))
                login()
            else
                Toast.makeText(this, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        }
        binding.layoutRememberMe.setOnClickListener {
            setRememberMe()
        }
        binding.mForgotPassword.setOnClickListener {
            onForgotPasswordClick()
        }
    }

    private fun setRememberMe(){
        if (binding.mRememberMeCheckbox.tag == 0){
            binding.mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_checked_login)
            binding.mRememberMeCheckbox.setTag(1)
            viewModel.isRememberMe = true
        }else{
            binding.mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_uncheck_login)
            binding.mRememberMeCheckbox.setTag(0)
            viewModel.isRememberMe = false
        }
    }

    private fun onForgotPasswordClick() {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun setLoginObserver(loginRequest: LoginRequest) {
        viewModel.loginUser(loginRequest).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.mContinue.visibility = View.VISIBLE
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setResponse(response: LoginResponse) {
        binding.mProgressBars.visibility = View.GONE
        binding.mContinue.visibility = View.VISIBLE
        viewModel.setResponse(response)

        if(response.success){
//            showResetPassword()
        }
        else{
            binding.mProgressBars.visibility = View.GONE
            binding.mContinue.visibility = View.VISIBLE
            Toast.makeText(this, "${response.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login() {
        if (viewModel.token.isNullOrEmpty()){
            getFirebaseToken()
        }

        if (validateLoginDetails()) {
            onLoginFailed()
            return
        }
        hideKeyboard()
        binding.mContinue!!.visibility = View.GONE
        val email = binding.mEmailTextField.editText!!.text.toString().trim()
        val password = binding.mPasswordTextField.editText!!.text.toString().trim()
        val loginRequest = viewModel.getLoginRequest(email, password)
        setLoginObserver(loginRequest)
    }

    private fun validateLoginDetails(): Boolean {
        binding.mEmailTextField!!.error = viewModel.validateEmail(
            binding.mEmailTextField.editText!!.text.toString().trim(),
            resources.getString(R.string.validate_email)
        )

        binding.mPasswordTextField!!.error = viewModel.validatePassword(
            binding.mPasswordTextField.editText!!.text.toString().trim(),
            resources.getString(R.string.validate_password)
        )

        return (viewModel.validEmail || viewModel.validPassword)
    }

    private fun onLoginFailed() {
        binding.mContinue!!.isEnabled = true
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            LoginViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(LoginViewModel::class.java)

        viewModel.login?.observe(this, Observer {
            checkUserData()
        })
        viewModel.isInvalid?.observe(this, Observer {
            it?.let {
                //Reset status value at first to prevent multitriggering
                //and to be available to trigger action again
                viewModel.isInvalid?.value = null
             //   toast(viewModel.message!!)
                Toast.makeText(this, viewModel.message!!, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkUserData(){
        viewModel.login?.value?.userDetails?.let {
            showResetPassword()
        } ?: kotlin.run {

        }
    }

    private fun getFirebaseToken(){
        viewModel.token = AppPreferences.fireBaseToken
        Log.w("LoginActivity", "getFirebaseToken: ${viewModel.token}", )
    }

    private fun gotoDashboard(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showResetPassword() {
        val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//        finish()
    }

    private fun setNoInternetSnackbar(){
        // No Internet Snackbar: Fire
        NoInternetSnackbarFire.Builder(
            binding.mainLayout,
            lifecycle
        ).apply {
            snackbarProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {

                    }
                }

                duration = Snackbar.LENGTH_INDEFINITE // Optional
                noInternetConnectionMessage = "No active Internet connection!" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode!" // Optional
                snackbarActionText = "Settings" // Optional
                showActionToDismiss = false // Optional
                snackbarDismissActionText = "OK" // Optional
            }
        }.build()
    }
}