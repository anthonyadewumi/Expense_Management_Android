package com.bonhams.expensemanagement.ui.claims.splitClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class SplitClaimViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplitClaimViewModel::class.java)) {
            return SplitClaimViewModel(SplitClaimRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}