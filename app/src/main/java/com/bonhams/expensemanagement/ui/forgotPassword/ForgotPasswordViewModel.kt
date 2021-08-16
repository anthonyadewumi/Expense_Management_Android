package com.bonhams.expensemanagement.ui.forgotPassword

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.ForgotPasswordRequest
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class ForgotPasswordViewModel(private val forgotPassword: ForgotPasswordRepository): ViewModel(){

    var login: MutableLiveData<LoginResponse>? = null
    var validEmail = true
    var validPassword = true
    var isRememberMe = false
    var isInvalid: MutableLiveData<Boolean>? = null
    var message: String? = null
    var token: String? = null
    var loginData: LoginResponse? = null
    init {
        login = MutableLiveData()
        isInvalid = MutableLiveData()
    }

    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = forgotPassword.forgotPassword(forgotPasswordRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getForgotPasswordRequest(email: String): ForgotPasswordRequest {
        val loginRequest = ForgotPasswordRequest()
        loginRequest.email = email
        return loginRequest
    }

    fun validateEmail(email: String, error: String): String? {
        var errorStr: String? = null

        if (email.isEmpty()){
            errorStr = error
            validEmail = true
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isDigitsOnly(email)){
            errorStr = error
            validEmail = true
        }
        else {
            validEmail = false
        }

        return errorStr
    }
}