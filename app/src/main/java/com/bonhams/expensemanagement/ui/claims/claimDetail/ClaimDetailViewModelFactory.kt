package com.bonhams.expensemanagement.ui.claims.claimDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ClaimDetailViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClaimDetailViewModel::class.java)) {
            return ClaimDetailViewModel(ClaimDetailRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}