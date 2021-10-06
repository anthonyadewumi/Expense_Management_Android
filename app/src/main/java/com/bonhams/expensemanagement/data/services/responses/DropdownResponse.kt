package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.*
import com.google.gson.annotations.SerializedName

class DropdownResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("carType")
    val carType: List<CarType> = emptyList()
    @SerializedName("companyList")
    val companyList: List<Company> = emptyList()
    @SerializedName("countryList")
    val countryList: List<Country> = emptyList()
    @SerializedName("currencyType")
    val currencyType: List<Currency> = emptyList()
    @SerializedName("departmentList")
    val departmentList: List<Department> = emptyList()
    @SerializedName("expenseGroup")
    val expenseGroup: List<ExpenseGroup> = emptyList()
    @SerializedName("expenseType")
    val expenseType: List<ExpenseType> = emptyList()
    @SerializedName("mileageType")
    val mileageType: List<MileageType> = emptyList()
    @SerializedName("statusType")
    val statusType: List<StatusType> = emptyList()
    @SerializedName("userType")
    val userType: List<UserType>? = null
    @SerializedName("tax")
    val tax: List<Tax> = emptyList()
}