package com.bonhams.expensemanagement.ui.claims

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bonhams.expensemanagement.data.services.ApiHelper

class ClaimsViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val apiHelper: ApiHelper) :
    AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ClaimsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClaimsViewModel(ClaimsByPageRepository(apiHelper), handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}