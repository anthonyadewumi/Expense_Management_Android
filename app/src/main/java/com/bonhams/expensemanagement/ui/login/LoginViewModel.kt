package com.bonhams.expensemanagement.ui.login

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel(){

    var login: MutableLiveData<LoginResponse>? = null
    var validEmail = true
    var validPassword = true
    var isRememberMe = false
    var isInvalid: MutableLiveData<Boolean>? = null
    var message: String? = null
    var loginData: LoginResponse? = null
    init {
        login = MutableLiveData()
        isInvalid = MutableLiveData()
    }

    fun loginUser(loginRequest: LoginRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = loginRepository.getLogin(loginRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getLoginRequest(email: String, password: String): LoginRequest {
        val loginRequest = LoginRequest()
        loginRequest.username = email
        loginRequest.password = password
//        loginRequest.deviceToken = token
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

    fun setResponse(response: LoginResponse) {
        if(response.success) {
            login?.value = response
            AppPreferences.isLoggedIn = isRememberMe && (response.userDetails?.token ?: "").trim().isNotEmpty()
            AppPreferences.isTokenAvailable = (response.userDetails?.token ?: "").trim().isNotEmpty()
            AppPreferences.userToken = response.userDetails?.token ?: ""
            AppPreferences.refreshToken = response.userDetails?.refreshToken ?: ""
            AppPreferences.userId = response.userDetails?.id ?: ""
            AppPreferences.employeeId = response.userDetails?.employID ?: ""
            AppPreferences.fullName = response.userDetails?.name ?: ""
            AppPreferences.firstName = response.userDetails?.fname ?: ""
            AppPreferences.lastName = response.userDetails?.lname ?: ""
            AppPreferences.email = response.userDetails?.email ?: ""
            AppPreferences.company = response.userDetails?.companyName ?: ""
            AppPreferences.carId = response.userDetails?.carType_id ?: ""
            AppPreferences.carType = response.userDetails?.carType ?: ""
            AppPreferences.userType = response.userDetails?.userType ?: ""
            AppPreferences.department = response.userDetails?.departmentName ?: ""
            AppPreferences.departmentID = response.userDetails?.departmentId ?: ""
            AppPreferences.profilePic = response.userDetails?.profileImage ?: ""
            AppPreferences.phoneNumber = response.userDetails?.contactNo ?: ""
            AppPreferences.ledgerId = response.userDetails?.ledger_id ?: ""

            Log.d("LoginActivity", "setResponse: ${AppPreferences.userId}  Name: ${AppPreferences.firstName} ${AppPreferences.lastName}")
        }
        else {
            message = response.message
            isInvalid?.value = true
        }
    }

    fun responseToString() :String{
        val gson = Gson()
        val json = gson.toJson(loginData)
        Log.i("LoginViewModel", "responseToString: $json", )
        return json
    }

}