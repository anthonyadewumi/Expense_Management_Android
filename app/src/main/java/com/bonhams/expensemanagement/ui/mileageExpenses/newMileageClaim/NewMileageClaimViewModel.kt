package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class NewMileageClaimViewModel(private val mileageClaimRepository: NewMileageClaimRepository) : ViewModel() {

    lateinit var companyList: List<Company>
    lateinit var departmentList: List<Department>
    lateinit var expenseTypeList: List<ExpenseType>
    lateinit var distanceList: List<ExpenseGroup>
    lateinit var carTypeList: List<CarType>
    lateinit var currencyList: List<Currency>
    lateinit var mileageTypeList: List<MileageType>
   // var attachmentsList: MutableList<String> = mutableListOf()
    var attachmentsList: MutableList<String?> = mutableListOf<String?>()


    fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageClaimRepository.createNewMileageClaim(mileageClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDropDownData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageClaimRepository.dropdownData()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getNewMileageClaimRequest(title: String,companyName: String, mileageType: String, department: String,
                                  dateSubmitted: String, expenseType: String, MerchantName: String,
                                  tripDate: String, tripFrom: String, tripTo: String,
                                  distance: String, carType: String, claimedMiles: String,
                                  roundTrip: Boolean, currency: String, petrolAmount: String,
                                  parkAmount: String, totalAmount: String, tax: String,
                                  netAmount: String, description: String, attachments: List<String>
                                ): NewMileageClaimRequest {

        val newClaimRequest = NewMileageClaimRequest()
        newClaimRequest.title = title
        newClaimRequest.companyName = companyName 
        newClaimRequest.mileageType = mileageType
        newClaimRequest.department = department
        newClaimRequest.dateSubmitted = dateSubmitted
        newClaimRequest.expenseType = expenseType
        newClaimRequest.merchantName = MerchantName
        newClaimRequest.tripDate = tripDate
        newClaimRequest.tripFrom = tripFrom
        newClaimRequest.tripTo = tripTo
        newClaimRequest.distance = distance
        newClaimRequest.carType = carType
        newClaimRequest.claimedMiles = claimedMiles
        newClaimRequest.roundTrip = if(roundTrip) "1" else "0"
        newClaimRequest.currency = currency
        newClaimRequest.petrolAmount = petrolAmount
        newClaimRequest.parkAmount = parkAmount
        newClaimRequest.totalAmount = totalAmount
        newClaimRequest.tax = tax
        newClaimRequest.netAmount = netAmount
        newClaimRequest.description = description
        newClaimRequest.attachments = attachments
        
        return newClaimRequest
    }

    fun validateNewClaimRequest(newClaimRequest: NewMileageClaimRequest): Pair<Boolean, Int>{
        var isValid: Pair<Boolean, Int> = Pair(true, R.string.ok)

        if(newClaimRequest.title.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_title)
        else if(newClaimRequest.mileageType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_mileage_type)
        else if(newClaimRequest.department.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_department)
        else if(newClaimRequest.expenseType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_expense_type)
        else if(newClaimRequest.merchantName.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_merchant_name)
        else if(newClaimRequest.tripFrom.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_starting_location)
        else if(newClaimRequest.tripTo.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_end_location)
        else if(newClaimRequest.distance.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_distance)
        else if(newClaimRequest.carType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_car_type)
        else if(newClaimRequest.claimedMiles.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_claimed_miles)
        else if(newClaimRequest.currency.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_currency)
        else if(newClaimRequest.totalAmount.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_total_amount)
        else if(newClaimRequest.tax.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_tax)

        return isValid
    }
}