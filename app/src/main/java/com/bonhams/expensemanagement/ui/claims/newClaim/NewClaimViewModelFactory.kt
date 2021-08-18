package com.bonhams.expensemanagement.ui.claims.newClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class NewClaimViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewClaimViewModel::class.java)) {
            return NewClaimViewModel(NewClaimRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}