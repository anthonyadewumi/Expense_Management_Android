package com.bonhams.expensemanagement.ui.forgotPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ForgotPasswordViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(ForgotPasswordRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}