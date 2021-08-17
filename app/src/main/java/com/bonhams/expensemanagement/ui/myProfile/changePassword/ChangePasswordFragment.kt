package com.bonhams.expensemanagement.ui.myProfile.changePassword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.ChangePasswordRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var edtOldPassword: EditText? = null
    private var edtNewPassword: EditText? = null
    private var edtConfirmPassword: EditText? = null
    private var mProgressBar: ProgressBar? = null
    private var btnReset: AppCompatButton? = null
    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        contextActivity = activity as? BaseActivity

        edtOldPassword = view.findViewById(R.id.edtOldPassword)
        edtNewPassword = view.findViewById(R.id.edtNewPassword)
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        btnReset = view.findViewById(R.id.btnReset)

        setClickListeners()
        setupViewModel()
        return view
    }

    private fun setClickListeners(){
        btnReset?.setOnClickListener(View.OnClickListener {
            changePasswordPassword()
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ChangePasswordViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ChangePasswordViewModel::class.java)
    }

    private fun changePasswordPassword() {
        if (validateChangePassword()) {
            onChangePasswordFailed()
            return
        }

        btnReset!!.visibility = View.GONE
        val oldPassword = edtOldPassword?.text.toString().trim()
        val newPassword = edtNewPassword?.text.toString().trim()
        val changePasswordRequest = viewModel.getChangePasswordRequest(oldPassword, newPassword)
        setChangePasswordObserver(changePasswordRequest)
    }

    private fun setChangePasswordObserver(changePasswordRequest: ChangePasswordRequest) {
        viewModel.getChangePassword(changePasswordRequest).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        mProgressBars.visibility = View.GONE
                        btnReset?.visibility = View.VISIBLE
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun validateChangePassword(): Boolean {
        edtOldPassword?.error = viewModel.validatePassword(
            edtOldPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password),
            true
        )

        edtNewPassword?.error = viewModel.validatePassword(
            edtNewPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password),
            false
        )

        edtConfirmPassword?.error = viewModel.validateConfirmPassword(
            edtNewPassword?.text.toString().trim(),
            edtConfirmPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password_not_match)
        )

        return (viewModel.validOldPassword || viewModel.validPassword || viewModel.validConfirmPassword)
    }

    private fun setResponse(commonResponse: CommonResponse) {
        mProgressBars.visibility = View.GONE
        btnReset?.visibility = View.VISIBLE
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
    }

    private fun onChangePasswordFailed() {
        btnReset?.isEnabled = true
    }

}