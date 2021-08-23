package com.bonhams.expensemanagement.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonhams.expensemanagement.utils.ClaimStatus

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    val datePicker = MutableLiveData<Any>()
    val statusPicker = MutableLiveData<Any>()

    fun setDatePickerCommunicator(date: Pair<String, String>){
        datePicker.value = date
    }

    fun setStatusPickerCommunicator(status: ClaimStatus){
        statusPicker.value = status
    }
}