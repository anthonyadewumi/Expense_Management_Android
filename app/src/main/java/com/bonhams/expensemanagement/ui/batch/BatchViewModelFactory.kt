package com.bonhams.expensemanagement.ui.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.data.services.ApiHelper

class BatchViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BatchViewModel::class.java)) {
            return BatchViewModel(BatchRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}