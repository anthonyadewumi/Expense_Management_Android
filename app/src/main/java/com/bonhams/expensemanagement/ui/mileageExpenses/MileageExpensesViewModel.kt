package com.bonhams.expensemanagement.ui.mileageExpenses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.responses.MileageListResponse
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class MileageExpensesViewModel(private val mileageRepository: MileageExpensesRepository) : ViewModel() {

    var responseMileageList: MutableLiveData<MileageListResponse>? = null

    init {
        responseMileageList = MutableLiveData()
    }

    fun getMileageExpensesList(mileageRequest: MileageExpenseRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageRepository.getMileageExpensesList(mileageRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getMileageListRequest(page: Int, search: String): MileageExpenseRequest {
        val claimListRequest = MileageExpenseRequest()
        claimListRequest.page = page
        return claimListRequest
    }
}