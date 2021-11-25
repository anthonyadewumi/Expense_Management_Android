package com.bonhams.expensemanagement.ui.claims.newClaim

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody

class NewClaimViewModel(private val newClaimRepository: NewClaimRepository) : ViewModel() {

    lateinit var expenseGroupList: List<ExpenseGroup>
    var expenseTypeList: MutableList<ExpenseType> = mutableListOf<ExpenseType>()
    lateinit var expenseTypeListExpenseGroup: List<ExpenseType>
    lateinit var departmentListCompany: List<Department>
    var departmentList: MutableList<Department> = mutableListOf<Department>()

    //lateinit var currencyList: List<Currency>
    var currencyList: MutableList<Currency> = mutableListOf<Currency>()
    lateinit var companyList: List<Company>
    lateinit var carTypeList: List<CarType>
    lateinit var mileageTypeList: List<MileageType>
    lateinit var statusTypeList: List<StatusType>
    lateinit var taxList: List<Tax>

    //var attachmentsList: List<String> = mutableListOf()
    var attachmentsList: MutableList<String?> = mutableListOf<String?>()
    var claimImageList: MutableList<String?> = mutableListOf<String?>()
    fun createNewClaim(newClaimRequest: NewClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.createNewClaim(newClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun uploadClaimAttachement(claimImage :List<MultipartBody.Part>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = newClaimRepository.uploadImage(claimImage)))
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

    fun getNewClaimRequest(title: String, merchantName: String, expenseGroup: String, expenseType: String,
                           companyNumber: String, department: String, dateSubmitted: String,
                           currency: String, totalAmount: String, tax: String,
                           netAmount: String, description: String, taxCode: String, auction: String,expenseCode: String,claimImage: List<String>,
                           attachments: List<String>): NewClaimRequest {
        val newClaimRequest = NewClaimRequest()
        newClaimRequest.title = title
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
        newClaimRequest.taxCode = taxCode
        if(auction.isNullOrEmpty()){
            newClaimRequest.auction ="0"
            newClaimRequest.expenseCode ="0"
        }else{
            newClaimRequest.auction = auction
            newClaimRequest.expenseCode = expenseCode
        }

        newClaimRequest.attachments = attachments
      //  newClaimRequest.claimImage = claimImage
        return newClaimRequest
    }

    fun validateNewClaimRequest(newClaimRequest: NewClaimRequest): Pair<Boolean, Int>{
        var isValid: Pair<Boolean, Int> = Pair(true, R.string.ok)

        if(newClaimRequest.title.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_company_numer)
        else if(newClaimRequest.expenseGroup.isNullOrEmpty())
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