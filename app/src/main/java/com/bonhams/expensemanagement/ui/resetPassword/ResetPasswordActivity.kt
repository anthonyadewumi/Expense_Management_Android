package com.bonhams.expensemanagement.ui.resetPassword

import android.content.Intent
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
import com.bonhams.expensemanagement.data.services.requests.ResetPasswordRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.ActivityResetPasswordBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils


class ResetPasswordActivity : BaseActivity() {


    private lateinit var viewModel: ResetPasswordViewModel
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)

        setClickListeners()
        setupViewModel()
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
                resetPassword()
            else
                Toast.makeText(this, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ResetPasswordViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ResetPasswordViewModel::class.java)
    }

    private fun resetPassword() {
        if (validateResetPassword()) {
            onResetPasswordFailed()
            return
        }

        binding.mContinue!!.visibility = View.GONE
        val password = binding.mPassword.text.toString().trim()
        val resetPasswordRequest = viewModel.getForgotPasswordRequest(password)
        setResetPasswordObserver(resetPasswordRequest)
    }

    private fun setResetPasswordObserver(resetPasswordRequest: ResetPasswordRequest) {
        viewModel.forgotPassword(resetPasswordRequest).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setResetPasswordObserver: ${resource.status}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.mContinue.visibility = View.VISIBLE
                        Log.e(TAG, "setupObservers: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun validateResetPassword(): Boolean {
        binding.mPassword!!.error = viewModel.validatePassword(
            binding.mPassword.text.toString().trim(),
            resources.getString(R.string.validate_password)
        )

        binding.mConfirmPassword!!.error = viewModel.validateConfirmPassword(
            binding.mPassword.text.toString().trim(),
            binding.mConfirmPassword.text.toString().trim(),
            resources.getString(R.string.validate_password_not_match)
        )

        return (viewModel.validPassword || viewModel.validConfirmPassword)
    }

    private fun setResponse(commonResponse: CommonResponse) {
        binding.mProgressBars.visibility = View.GONE
        binding.mContinue.visibility = View.VISIBLE
        Toast.makeText(this, commonResponse.message, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun onResetPasswordFailed() {
        binding.mContinue!!.isEnabled = true
    }

    private fun setNoInternetSnackbar(){
        // No Internet Snackbar: Fire
        NoInternetSnackbarFire.Builder(
            binding.resetPassMainLayout,
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