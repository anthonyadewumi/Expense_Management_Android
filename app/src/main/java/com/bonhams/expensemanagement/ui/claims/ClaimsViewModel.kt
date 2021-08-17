package com.bonhams.expensemanagement.ui.claims

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimsResponse
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class ClaimsViewModel(private val claimsRepository: ClaimsRepository) : ViewModel() {

    var responseClaimsList: MutableLiveData<ClaimsResponse>? = null

    init {
        responseClaimsList = MutableLiveData()
    }

    fun getClaimsList(claimsRequest: ClaimsRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = claimsRepository.getClaimsList(claimsRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getClaimsRequest(page: Int, search: String): ClaimsRequest {
        val claimListRequest = ClaimsRequest()
        claimListRequest.page = page
        claimListRequest.searchKey = search
        return claimListRequest
    }
}