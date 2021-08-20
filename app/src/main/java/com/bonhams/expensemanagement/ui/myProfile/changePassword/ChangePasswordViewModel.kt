package com.bonhams.expensemanagement.ui.myProfile.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.ChangePasswordRequest
import com.bonhams.expensemanagement.data.services.responses.MileageListResponse
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

class ChangePasswordViewModel(private val changePasswordRepository: ChangePasswordRepository) : ViewModel() {

    var responseMileageList: MutableLiveData<MileageListResponse>? = null
    var validOldPassword = true
    var validPassword = true
    var validConfirmPassword = true

    init {
        responseMileageList = MutableLiveData()
    }

    fun getChangePassword(changePasswordRequest: ChangePasswordRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = changePasswordRepository.getChangePassword(changePasswordRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getChangePasswordRequest(oldPassword: String, newPassword: String): ChangePasswordRequest {
        val changePasswordRequest = ChangePasswordRequest()
        changePasswordRequest.password = oldPassword
        changePasswordRequest.newPassword = newPassword
        return changePasswordRequest
    }

    fun validatePassword(password: String, error: String, isOldPass: Boolean) : String?{
        var errorStr: String? = null
        if (password.isEmpty()){
            errorStr = error
            if(isOldPass)
                validOldPassword = true
            else
                validPassword = true
        }
        else {
            if(isOldPass)
                validOldPassword = false
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