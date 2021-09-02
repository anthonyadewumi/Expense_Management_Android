package com.bonhams.expensemanagement.ui.claims.claimDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class ClaimDetailViewModel(private val claimDetailRepository: ClaimDetailRepository) : ViewModel() {

    var attachmentsList: MutableList<String?> = mutableListOf()

    fun createNewClaim(newClaimRequest: NewClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = claimDetailRepository.createNewClaim(newClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}