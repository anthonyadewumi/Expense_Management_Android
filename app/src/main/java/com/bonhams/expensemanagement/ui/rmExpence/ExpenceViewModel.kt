package com.bonhams.expensemanagement.ui.rmExpence

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody

class ExpenceViewModel(private val newClaimRepository: ExpenceRepository) : ViewModel() {
    private val toBeAcceptedDataList = MutableLiveData<List<ToBeAcceptedData>>()
    val dateRange = MutableLiveData<Any?>()
    val datePicker = MutableLiveData<Any?>()
    val statusPicker = MutableLiveData<Any?>()

    fun resetFilters() {
        statusPicker.value = null
        dateRange.value = null
        datePicker.value = null
    }
    fun getRequestExpences(mileageExpenseRequest: JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.getRequestExpences(mileageExpenseRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun getRequestExpencesDetails(mileageExpenseRequest: JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.getRequestExpencesDetails(mileageExpenseRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun acceptReject(data: JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.acceptReject(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun acceptRejectBatch(data: JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.acceptRejectBatch(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun getDetails(claimId: JsonObject,mclaimId: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.getDetails(claimId,mclaimId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun getMielageDetails(claimId: JsonObject,mclaimId: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.getMielageDetails(claimId,mclaimId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun deleteBatch(requestData:JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.deleteBatch(requestData)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun submitBatch(requestData:JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.submitBatch(requestData)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}