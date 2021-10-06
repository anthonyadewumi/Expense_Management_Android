package com.bonhams.expensemanagement.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {

    val dateRange = MutableLiveData<Any?>()
    val datePicker = MutableLiveData<Any?>()
    val statusPicker = MutableLiveData<Any?>()

    fun resetFilters() {
        statusPicker.value = null
        dateRange.value = null
        datePicker.value = null
    }
}