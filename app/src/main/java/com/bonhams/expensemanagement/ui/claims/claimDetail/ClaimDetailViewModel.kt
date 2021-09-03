package com.bonhams.expensemanagement.ui.claims.claimDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.requests.DeleteClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class ClaimDetailViewModel(private val claimDetailRepository: ClaimDetailRepository) : ViewModel() {

    var attachmentsList: MutableList<String?> = mutableListOf()

    fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = claimDetailRepository.deleteClaim(deleteClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDeleteClaimRequest(claimDetail: ClaimDetail): DeleteClaimRequest{
        val deleteClaimRequest = DeleteClaimRequest()
        deleteClaimRequest.claimId = claimDetail.id

        return deleteClaimRequest
    }
}