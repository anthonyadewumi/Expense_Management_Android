package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.model.Currency
import com.bonhams.expensemanagement.data.services.requests.EditMileageClaimRequest
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.utils.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody
import java.util.*


class NewMileageClaimViewModel(private val mileageClaimRepository: NewMileageClaimRepository) : ViewModel() {

    var expenseGroupList: MutableList<ExpenseGroup> = mutableListOf<ExpenseGroup>()

    var expenseTypeList: MutableList<ExpenseType> = mutableListOf<ExpenseType>()
    lateinit var expenseTypeListExpenseGroup: List<ExpenseType>
    lateinit var departmentListCompany: List<Department>
    var departmentList: MutableList<Department> = mutableListOf<Department>()


    lateinit var companyList: List<Company>

    var expenseCode: MutableList<ExpenseCode> = mutableListOf<ExpenseCode>()
    lateinit var distanceList: List<ExpenseGroup>
    lateinit var carTypeList: List<CarType>
    var currencyList: MutableList<Currency> = mutableListOf<Currency>()
    lateinit var mileageTypeList: List<MileageType>
    lateinit var taxList: List<Tax>
    lateinit var MileageRateList: List<MilageRate>
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
    fun editNewMileageClaim(mileageClaimRequest: EditMileageClaimRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageClaimRepository.editNewMileageClaim(mileageClaimRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun uploadClaimAttachement(newClaimRequest: RequestBody) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageClaimRepository.uploadImage(newClaimRequest)))
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
    fun getDistance(origins:String,destinations:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val mapQuery: MutableMap<String, String> = HashMap()
            mapQuery["key"] = "AIzaSyBG514Hl7ekIEU3iyXKcnqBi0vvIgjtp-8"
            mapQuery["origins"] = origins
            mapQuery["destinations"] = destinations
            emit(Resource.success(data = mileageClaimRepository.getdistance(mapQuery)))
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
                                  netAmount: String, description: String,taxcode: String,auction: String,expencecode: String,mileageRate:String, attachments: List<String>,expenseGroup: String,
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
        if(petrolAmount.isEmpty()){
            newClaimRequest.petrolAmount = "0.0"

        }else{
            newClaimRequest.petrolAmount = petrolAmount

        }
        if(parkAmount.isEmpty()){
            newClaimRequest.parkAmount = "0.0"

        }else{
            newClaimRequest.parkAmount = parkAmount

        }
        newClaimRequest.totalAmount = totalAmount
        newClaimRequest.tax = tax
        newClaimRequest.netAmount = netAmount
        newClaimRequest.description = description
        newClaimRequest.taxCode = taxcode
        if(auction.isEmpty()){
            newClaimRequest.auction = "0"

        }else{
            newClaimRequest.auction = auction

        }
        if(expencecode.isEmpty()){
            newClaimRequest.expenseCode = null

        }else{
            newClaimRequest.expenseCode = expencecode

        }
        newClaimRequest.attachments = attachments
        newClaimRequest.expenseGroup = expenseGroup
        newClaimRequest.mileage_rate = mileageRate

        return newClaimRequest
    }
    fun getMileageEditRequest(title: String,companyName: String, mileageType: String, department: String,
                                  dateSubmitted: String, expenseType: String, MerchantName: String,
                                  tripDate: String, tripFrom: String, tripTo: String,
                                  distance: String, carType: String, claimedMiles: String,
                                  roundTrip: Boolean, currency: String, petrolAmount: String,
                                  parkAmount: String, totalAmount: String, tax: String,
                                  netAmount: String, description: String,taxcode: String,auction: String,expencecode: String,mileageRate:String, attachments: List<String>,expenseGroup: String,
                                  mainId:String,overall_status_id:String,rm_status_id:String,fm_status_id:String): EditMileageClaimRequest {

        val newClaimRequest = EditMileageClaimRequest()
        newClaimRequest.title = title
        newClaimRequest.companyName = companyName
        newClaimRequest.mileageType = mileageType
        newClaimRequest.department = department
       // newClaimRequest.dateSubmitted = dateSubmitted
        newClaimRequest.expenseType = expenseType
        newClaimRequest.merchantName = MerchantName
        newClaimRequest.tripDate = tripDate
        newClaimRequest.tripFrom = tripFrom
        newClaimRequest.tripTo = tripTo
        newClaimRequest.distance = distance
        newClaimRequest.carType = carType
        newClaimRequest.claimedMiles = claimedMiles
        newClaimRequest.overall_status_id = overall_status_id
        newClaimRequest.rm_status_id = rm_status_id
        newClaimRequest.fm_status_id = fm_status_id
        newClaimRequest.roundTrip = if(roundTrip) "1" else "0"
        newClaimRequest.currency = currency
        if(petrolAmount.isEmpty()){
            newClaimRequest.petrolAmount = "0.0"

        }else{
            newClaimRequest.petrolAmount = petrolAmount

        }
        if(parkAmount.isEmpty()){
            newClaimRequest.parkAmount = "0.0"

        }else{
            newClaimRequest.parkAmount = parkAmount

        }
        newClaimRequest.totalAmount = totalAmount
        newClaimRequest.tax = tax
        newClaimRequest.netAmount = netAmount
        newClaimRequest.description = description
        newClaimRequest.taxCode = taxcode
        if(auction.isEmpty()){
            newClaimRequest.auction = "0"

        }else{
            newClaimRequest.auction = auction

        }
        if(expencecode.isEmpty()){
            newClaimRequest.expenseCode = null

        }else{
            newClaimRequest.expenseCode = expencecode

        }
        newClaimRequest.expenseGroup = expenseGroup
        newClaimRequest.mileage_rate = mileageRate
        newClaimRequest.main_id = mainId

        return newClaimRequest
    }

    fun validateNewClaimRequest(newClaimRequest: NewMileageClaimRequest): Pair<Boolean, Int>{
        var isValid: Pair<Boolean, Int> = Pair(true, R.string.ok)

        if(newClaimRequest.title.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_company_numer)
        else if(newClaimRequest.mileageType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_mileage_type)
        else if(newClaimRequest.department.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_department)
        else if(newClaimRequest.expenseType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_expense_type)
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
    fun validateEditClaimRequest(newClaimRequest: EditMileageClaimRequest): Pair<Boolean, Int>{
        var isValid: Pair<Boolean, Int> = Pair(true, R.string.ok)

        if(newClaimRequest.title.isNullOrEmpty())
            isValid = Pair(false, R.string.please_enter_company_numer)
        else if(newClaimRequest.mileageType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_mileage_type)
        else if(newClaimRequest.department.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_department)
        else if(newClaimRequest.expenseType.isNullOrEmpty())
            isValid = Pair(false, R.string.please_select_expense_type)
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