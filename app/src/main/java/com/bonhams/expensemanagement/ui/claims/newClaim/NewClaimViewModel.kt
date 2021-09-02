package com.bonhams.expensemanagement.ui.claims.newClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class NewClaimViewModel(private val newClaimRepository: NewClaimRepository) : ViewModel() {

    lateinit var expenseGroupList: List<ExpenseGroup>
    lateinit var expenseTypeList: List<ExpenseType>
    lateinit var departmentList: List<Department>
    lateinit var currencyList: List<Currency>
    lateinit var companyList: List<Company>
    lateinit var carTypeList: List<CarType>
    lateinit var mileageTypeList: List<MileageType>
    lateinit var statusTypeList: List<StatusType>

    var attachmentsList: MutableList<String> = mutableListOf()

    fun createNewClaim(newClaimRequest: NewClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.createNewClaim(newClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDropDownData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.dropdownData()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getNewClaimRequest(merchantName: String, expenseGroup: String, expenseType: String,
                           companyNumber: String, department: String, dateSubmitted: String,
                           currency: String, totalAmount: String, tax: String,
                           netAmount: String, description: String,
                           attachments: List<String>): NewClaimRequest {
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
        newClaimRequest.title = description
        newClaimRequest.description = description
        newClaimRequest.attachments = attachments
        return newClaimRequest
    }

    fun validateNewClaimRequest(newClaimRequest: NewClaimRequest): Pair<Boolean, Int>{
        var isValid: Pair<Boolean, Int> = Pair(true, R.string.ok)

        if(newClaimRequest.expenseGroup.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_expense_group)
        else if(newClaimRequest.expenseType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_expense_type)
        else if(newClaimRequest.department.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_department)
        else if(newClaimRequest.currency.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_currency)
        else if(newClaimRequest.totalAmount.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_total_amount)
        else if(newClaimRequest.tax.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_tax)

        return isValid
    }
}