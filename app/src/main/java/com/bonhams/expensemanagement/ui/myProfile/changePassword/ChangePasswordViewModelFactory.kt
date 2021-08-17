package com.bonhams.expensemanagement.ui.myProfile.changePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ChangePasswordViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(ChangePasswordRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}