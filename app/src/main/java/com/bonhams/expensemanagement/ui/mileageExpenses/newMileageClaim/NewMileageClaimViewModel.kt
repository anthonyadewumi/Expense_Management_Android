package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers

class NewMileageClaimViewModel(private val mileageClaimRepository: NewMileageClaimRepository) : ViewModel() {

    lateinit var departmentList: List<Department>
    lateinit var expenseTypeList: List<ExpenseType>
    lateinit var distanceList: List<ExpenseGroup>
    lateinit var carTypeList: List<CarType>
    lateinit var currencyList: List<Currency>
    var attachmentsList: MutableList<String> = mutableListOf()

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

    fun getNewMileageClaimRequest(companyName: String, mileageType: String, department: String,
                                  dateSubmitted: String, expenseType: String, MerchantName: String,
                                  tripDate: String, tripFrom: String, tripTo: String,
                                  distance: String, carType: String, claimedMiles: String,
                                  roundTrip: String, currency: String, petrolAmount: String,
                                  parkAmount: String, totalAmount: String, tax: String,
                                  netAmount: String, description: String, attachments: String
                                ): NewMileageClaimRequest {

        val newClaimRequest = NewMileageClaimRequest()
        newClaimRequest.companyName = companyName 
        newClaimRequest.mileageType = mileageType
        newClaimRequest.department = department
        newClaimRequest.dateSubmitted = dateSubmitted
        newClaimRequest.expenseType = expenseType
        newClaimRequest.MerchantName = MerchantName
        newClaimRequest.tripDate = tripDate
        newClaimRequest.tripFrom = tripFrom
        newClaimRequest.tripTo = tripTo
        newClaimRequest.distance = distance
        newClaimRequest.carType = carType
        newClaimRequest.claimedMiles = claimedMiles
        newClaimRequest.roundTrip = roundTrip
        newClaimRequest.currency = currency
        newClaimRequest.petrolAmount = petrolAmount
        newClaimRequest.parkAmount = parkAmount
        newClaimRequest.totalAmount = totalAmount
        newClaimRequest.tax = tax
        newClaimRequest.netAmount = netAmount
        newClaimRequest.description = description
//        newClaimRequest.attachments = attachments
        
        return newClaimRequest
    }
}