package com.bonhams.expensemanagement.ui.main

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
}
