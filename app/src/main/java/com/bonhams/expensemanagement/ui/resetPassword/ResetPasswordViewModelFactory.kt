package com.bonhams.expensemanagement.ui.resetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ResetPasswordViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            return ResetPasswordViewModel(ResetPasswordRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}