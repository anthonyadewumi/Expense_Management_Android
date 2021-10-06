package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.GoogleApiHelper

class NewMileageClaimViewModelFactory(private val apiHelper: ApiHelper,private val googleApiHelper: GoogleApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewMileageClaimViewModel::class.java)) {
            return NewMileageClaimViewModel(NewMileageClaimRepository(apiHelper,googleApiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}