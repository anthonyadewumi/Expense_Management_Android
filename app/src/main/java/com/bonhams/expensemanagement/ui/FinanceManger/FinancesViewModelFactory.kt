package com.bonhams.expensemanagement.ui.FinanceManger

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bonhams.expensemanagement.data.services.ApiHelper

class FinancesViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val apiHelper: ApiHelper) :
    AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(FinaanceMangerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinaanceMangerViewModel(RequestByPageRepository(apiHelper), handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}