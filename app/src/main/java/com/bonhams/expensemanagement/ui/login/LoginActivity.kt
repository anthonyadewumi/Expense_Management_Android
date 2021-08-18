package com.bonhams.expensemanagement.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class LoginActivity : BaseActivity() {
    private lateinit var viewModel: LoginViewModel
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupViewModel()
        setClickListeners()
        setNoInternetSnackbar()
    }

    private fun setClickListeners(){
        mContinue.setOnClickListener {
            if(NoInternetUtils.isConnectedToInternet(this))
                login()
            else
                Toast.makeText(this, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        }
        mRememberMeCheckbox.setOnClickListener {
            setRememberMe()
        }
        mForgotPassword.setOnClickListener {
            onForgotPasswordClick()
        }
    }

    private fun setRememberMe(){
        if (mRememberMeCheckbox.tag == 0){
            mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_checked_login)
            mRememberMeCheckbox.setTag(1)
            viewModel.isRememberMe = true
        }else{
            mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_uncheck_login)
            mRememberMeCheckbox.setTag(0)
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
                        mProgressBars.visibility = View.GONE
                        mContinue.visibility = View.VISIBLE
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setResponse(response: LoginResponse) {
        mProgressBars.visibility = View.GONE
        mContinue.visibility = View.VISIBLE
        viewModel.setResponse(response)

        if(response.success){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
        else{
            mProgressBars.visibility = View.GONE
            mContinue.visibility = View.VISIBLE
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
        mContinue!!.visibility = View.GONE
        val email = mEmailTextField.editText!!.text.toString().trim()
        val password = mPasswordTextField.editText!!.text.toString().trim()
        val loginRequest = viewModel.getLoginRequest(email, password)
        setLoginObserver(loginRequest)
    }

    private fun validateLoginDetails(): Boolean {
        mEmailTextField!!.error = viewModel.validateEmail(
            mEmailTextField.editText!!.text.toString().trim(),
            resources.getString(R.string.validate_email)
        )

        mPasswordTextField!!.error = viewModel.validatePassword(
            mPasswordTextField.editText!!.text.toString().trim(),
            resources.getString(R.string.validate_password)
        )

        return (viewModel.validEmail || viewModel.validPassword)
    }

    private fun onLoginFailed() {
        mContinue!!.isEnabled = true
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
            gotoDashboard()
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

    private fun setNoInternetSnackbar(){
        // No Internet Snackbar: Fire
        NoInternetSnackbarFire.Builder(
            mainLayout,
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