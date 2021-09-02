package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class MileageDetailClaimViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MileageDetailClaimViewModel::class.java)) {
            return MileageDetailClaimViewModel(MileageDetailClaimRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}