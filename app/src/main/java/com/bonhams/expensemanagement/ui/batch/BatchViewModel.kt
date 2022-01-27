package com.bonhams.expensemanagement.ui.batch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers

class BatchViewModel(private val batchRepository: BatchRepository) : ViewModel() {

    val dateRange = MutableLiveData<Any?>()
    val datePicker = MutableLiveData<Any?>()
    val statusPicker = MutableLiveData<Any?>()

    fun resetFilters() {
        statusPicker.value = null
        dateRange.value = null
        datePicker.value = null
    }
    fun getBatchData(requestData:JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = batchRepository.getBatchData(requestData)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}