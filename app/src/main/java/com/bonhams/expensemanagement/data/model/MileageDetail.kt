package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class MileageDetail (
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    val submittedOn: String = "",

    @SerializedName("status")
    val status: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    @SerializedName("group.id")
    val groupID: String = "",
    @SerializedName("group.name")
    val groupName: String = "",
    val type: String = "",
    @SerializedName("companyName")
    val companyName: String = "",
    val department: String = "",
    @SerializedName("currency_id")
    val currencyID: String = "",
    val currency: String = "",
    @SerializedName("currency_symbol")
    val currencySymbol: String = "",
    @SerializedName("currency_type_name")
    val currencyTypeNamename: String = "",
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
    @SerializedName("user_id")
    val userID: String = "",
    val createdBy: String = "",
    val attachments: String = "",
    @SerializedName("tax_code")
    val tax_code: String = "",
    @SerializedName("mileageRate")
    val mileageRate: String = "",
    @SerializedName("rm_updation_date")
    val rm_updation_date: String = "",
    @SerializedName("fm_updation_date")
    val fm_updation_date: String = ""
)