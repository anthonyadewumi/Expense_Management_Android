package com.bonhams.expensemanagement.ui.resetPassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.ResetPasswordRequest
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

class ResetPasswordViewModel(private val resetPassword: ResetPasswordRepository): ViewModel(){

    var login: MutableLiveData<LoginResponse>? = null
    var validConfirmPassword = true
    var validPassword = true
    var isInvalid: MutableLiveData<Boolean>? = null
    var message: String? = null


    init {
        login = MutableLiveData()
        isInvalid = MutableLiveData()
    }

    fun forgotPassword(resetPasswordRequest: ResetPasswordRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = resetPassword.resetPassword(resetPasswordRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getForgotPasswordRequest(password: String): ResetPasswordRequest {
        val loginRequest = ResetPasswordRequest()
        loginRequest.password = password
        return loginRequest
    }

    fun validatePassword(password: String, error: String) : String?{
        var errorStr: String? = null
        if (password.isEmpty()){
            errorStr = error
            validPassword = true
        }
        else {
            validPassword = false

            val pattern = Pattern.compile(Constants.PASSWORD_PATTERN)
            if (!pattern.matcher(password).matches()) {
                errorStr = error
                validPassword = true
            } else{
                validPassword = false
            }
        }

        return  errorStr
    }

    fun validateConfirmPassword(password: String, confirmPassword: String, error: String) : String?{
        var errorStr: String? = null
        if (password.isEmpty() || password != confirmPassword) {
            errorStr = error
            validConfirmPassword = true
        } else{
            validConfirmPassword = false
        }
        return  errorStr
    }
}