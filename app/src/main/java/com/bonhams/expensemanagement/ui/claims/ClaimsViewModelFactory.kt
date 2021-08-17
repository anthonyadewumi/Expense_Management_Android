package com.bonhams.expensemanagement.ui.claims

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ClaimsViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClaimsViewModel::class.java)) {
            return ClaimsViewModel(ClaimsRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}