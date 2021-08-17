package com.bonhams.expensemanagement.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

class LoginActivity : BaseActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupViewModel()
        setClickListeners()
        setNoInternetSnackbar()
    }

    fun setClickListeners(){
        mContinue.setOnClickListener {
            login()
        }
        mRememberMeCheckbox.setOnClickListener {
            setRememberMe()
        }
        mForgotPassword.setOnClickListener {
            onForgotPasswordClick()
        }
    }

    fun setRememberMe(){
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
                                Log.d("LoginActivity", "setLoginObserver: ${resource.data.message}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        mProgressBars.visibility = View.GONE
                        mContinue.visibility = View.VISIBLE
                        Log.e("LoginActivity", "setupObservers: ${it.message}")
                        it.message?.let { it1 -> Log.d("LoginActivity", "setLoginObserver: $it1") }
                    }
                    Status.LOADING -> {
                        mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun setResponse(response: LoginResponse) {
        mProgressBars.visibility = View.GONE
        mContinue.visibility = View.VISIBLE
        viewModel.setResponse(response)

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun login() {
        if (viewModel.token.isNullOrEmpty()){
            getFirebaseToken()
        }

        if (validate()) {
            onLoginFailed()
            return
        }

        mContinue!!.visibility = View.GONE
        val email = mEmailTextField.editText!!.text.toString()
        val password = mPasswordTextField.editText!!.text.toString()
        val loginRequest = viewModel.getLoginRequest(email, password)
        setLoginObserver(loginRequest)
    }

    fun validate(): Boolean {
        mEmailTextField!!.error = viewModel.validateEmail(
            mEmailTextField.editText!!.text.toString(),
            resources.getString(R.string.validate_email)
        )

        mPasswordTextField!!.error = viewModel.validatePassword(
            mPasswordTextField.editText!!.text.toString(),
            resources.getString(R.string.validate_password)
        )

        return (viewModel.validEmail || viewModel.validPassword)
    }

    fun onLoginFailed() {
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
            }
        })
    }

    private fun checkUserData(){
        viewModel.login?.value?.userDetails?.let {
           /* viewModel.loginData = LoginResponse.LoginInner().apply {
                userDetails = it
            }*/
            val jsonString = viewModel.responseToString()
            savePreference(jsonString)
            gotoDashboard()
        } ?: kotlin.run {
//            goToContactVerifaction()
        }
    }

    private fun savePreference(jsonString: String) {

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