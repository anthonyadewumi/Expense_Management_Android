package com.bonhams.expensemanagement.ui.main

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.responses.LogoutResponse
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var responseLogout: MutableLiveData<LogoutResponse>? = null
    var appbarSearchClick: MutableLiveData<Boolean>? = MutableLiveData(false)
    var appbarMenuClick: MutableLiveData<View>? = MutableLiveData()
    var appbarEditClick: MutableLiveData<View>? = MutableLiveData()
    var appbarSaveClick: MutableLiveData<View>? = MutableLiveData()
    var appbarbackClick: MutableLiveData<View>? = MutableLiveData()
    var validEmail = true

    init {
        responseLogout = MutableLiveData()
    }

    fun logoutUser() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.logoutUser()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun myprofile() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.profileDetail()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
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
