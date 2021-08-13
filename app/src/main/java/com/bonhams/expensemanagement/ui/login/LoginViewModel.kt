package com.bonhams.expensemanagement.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.model.LoginInner
import com.bonhams.expensemanagement.data.model.LoginResponse
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
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
    var token: String? = null
    var loginData: LoginInner? = null
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
        loginRequest.deviceToken = token
        return loginRequest
    }

    fun validateUsername(email: String, error: String): String? {
        var errorStr: String? = null

        if (email.isEmpty()){
            errorStr = error
            validEmail = true
        }
//        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isDigitsOnly(email)){
//            errorStr = error
//            validEmail = true
//        }
        else {
            validEmail = false
        }

        return errorStr
    }

    fun validatePassword(password: String, error: String) : String?{
        var errorStr: String? = null
        val pattern = Pattern.compile(Constants.PASSWORD_PATTERN)
        if (!pattern.matcher(password).matches()) {
            errorStr = error
            validPassword = true
        } else{
            validPassword = false
        }
        return  errorStr
    }

    fun setResponse(response: LoginResponse) {
        when {
            response.response?.code?.toInt() == 200 -> {
                login?.value = response
            }
            else -> {
                message = response.response?.message
                isInvalid?.value = true
            }
        }
    }

    fun responseToString() :String{
        val gson = Gson()
        val json = gson.toJson(loginData)
        Log.i("LoginViewModel", "responseToString: $json", )
        return json
    }

}