package com.bonhams.expensemanagement.ui.myProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.responses.LogoutResponse
import com.bonhams.expensemanagement.data.services.responses.MyProfileResponse
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class MyProfileViewModel(private val myProfileRepository: MyProfileRepository) : ViewModel() {
    var responseLogout: MutableLiveData<LogoutResponse>? = MutableLiveData()


    fun profileDetail() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = myProfileRepository.profileDetail()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setResponse(response: MyProfileResponse) {
        if(response.success) {
            AppPreferences.userId = response.profileDetail?.id ?: ""
            AppPreferences.employeeId = response.profileDetail?.employID ?: ""
            AppPreferences.fullName = response.profileDetail?.name ?: ""
            AppPreferences.firstName = response.profileDetail?.fname ?: ""
            AppPreferences.lastName = response.profileDetail?.lname ?: ""
            AppPreferences.email = response.profileDetail?.email ?: ""
            AppPreferences.profilePic = response.profileDetail?.profileImage ?: ""
            AppPreferences.phoneNumber = response.profileDetail?.contactNo ?: ""

            Log.d("LoginActivity", "setResponse: ${AppPreferences.userId}  Name: ${AppPreferences.firstName} ${AppPreferences.lastName}")
        }
    }
}