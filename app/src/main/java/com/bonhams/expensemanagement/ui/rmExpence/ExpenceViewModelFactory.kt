package com.bonhams.expensemanagement.ui.rmExpence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class ExpenceViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenceViewModel::class.java)) {
            return ExpenceViewModel(ExpenceRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}