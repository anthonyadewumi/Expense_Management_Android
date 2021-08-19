package com.bonhams.expensemanagement.ui.claims.splitClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.model.SpinnerItem
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class SplitClaimViewModel(private val splitClaimRepository: SplitClaimRepository) : ViewModel() {

    lateinit var expenseGroupList: List<SpinnerItem>
    lateinit var expenseTypeList: List<SpinnerItem>
    lateinit var departmentList: List<SpinnerItem>
    lateinit var currencyList: List<SpinnerItem>
    var attachmentsList: MutableList<String> = mutableListOf()

    fun createNewClaim(newClaimRequest: NewClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = splitClaimRepository.createNewClaim(newClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getNewClaimRequest(merchantName: String, expenseGroup: String, expenseType: String,
                           companyNumber: String, department: String, dateSubmitted: String,
                           currency: String, totalAmount: String, tax: String,
                           netAmount: String, description: String, attachments: String,
                           split: String): NewClaimRequest {
        val newClaimRequest = NewClaimRequest()
        newClaimRequest.merchantName = merchantName
        newClaimRequest.expenseGroup = expenseGroup
        newClaimRequest.expenseType = expenseType
        newClaimRequest.companyNumber = companyNumber
        newClaimRequest.department = department
        newClaimRequest.dateSubmitted = dateSubmitted
        newClaimRequest.currency = currency
        newClaimRequest.totalAmount = totalAmount
        newClaimRequest.tax = tax
        newClaimRequest.netAmount = netAmount
        newClaimRequest.description = description
        newClaimRequest.attachments = attachments
        newClaimRequest.split = split
        return newClaimRequest
    }
}