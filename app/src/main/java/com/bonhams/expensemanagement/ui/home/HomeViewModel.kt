package com.bonhams.expensemanagement.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    val datePicker = MutableLiveData<Any>()
    val statusPicker = MutableLiveData<Any>()

}