package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class MileageDetail (
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    val submittedOn: String = "",
    val status: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    val type: String = "",
    @SerializedName("companyname")
    val companyName: String = "",
    val department: String = "",
    @SerializedName("currency_id")
    val currencyID: String = "",
    val currency: String = "",
    val tax: String = "",
    val netAmount: String = "",
    val merchant: String = "",
    val tripDate: String = "",
    val isRoundTrip: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val distance: String = "",
    val claimedMileage: String = "",
    val parking: String = "",
    val petrolAmount: String = "",
    val carType: String = "",
    val createdBy: String? = null
)