package com.bonhams.expensemanagement.ui.forgotPassword

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.ForgotPasswordRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.ActivityForgotPasswordBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var viewModel: ForgotPasswordViewModel
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
//        setContentView(R.layout.activity_forgot_password)

        setupViewModel()
        setClickListeners()
        setNoInternetSnackbar()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun setClickListeners(){
        binding.ivBack.setOnClickListener(View.OnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.mContinue.setOnClickListener(View.OnClickListener {
            if(NoInternetUtils.isConnectedToInternet(this))
                forgotPassword()
            else
                Toast.makeText(this, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ForgotPasswordViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ForgotPasswordViewModel::class.java)
    }

    private fun forgotPassword() {
        if (validateForgotPassword()) {
            onForgotPasswordFailed()
            return
        }

        binding.mContinue!!.visibility = View.GONE
        val email = binding.mEmailTextField.editText!!.text.toString().trim()
        val forgotPasswordRequest = viewModel.getForgotPasswordRequest(email)
        setForgotPasswordObserver(forgotPasswordRequest)
    }

    private fun setForgotPasswordObserver(forgotPasswordRequest: ForgotPasswordRequest) {
        viewModel.forgotPassword(forgotPasswordRequest).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setForgotPasswordObserver: ${resource.status}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBar.visibility = View.GONE
                        binding.mContinue.visibility = View.VISIBLE
                        Log.e(TAG, "setupObservers: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun validateForgotPassword(): Boolean {
        binding.mEmailTextField!!.error = viewModel.validateEmail(
            binding.mEmailTextField.editText!!.text.toString().trim(),
            resources.getString(R.string.validate_email)
        )

        return (viewModel.validEmail)
    }

    private fun setResponse(commonResponse: CommonResponse) {
        binding.mProgressBar.visibility = View.GONE
        binding.mContinue.visibility = View.VISIBLE
        Toast.makeText(this, commonResponse.message, Toast.LENGTH_SHORT).show()
    }

    private fun onForgotPasswordFailed() {
        binding.mContinue!!.isEnabled = true
    }

    private fun setNoInternetSnackbar(){
        // No Internet Snackbar: Fire
        NoInternetSnackbarFire.Builder(
            binding.forgotPassMainLayout,
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