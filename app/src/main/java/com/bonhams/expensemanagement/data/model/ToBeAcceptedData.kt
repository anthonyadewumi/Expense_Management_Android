package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName


data class ToBeAcceptedData(
    @SerializedName("employeeId")
    val employeeId: Int = 0,
    @SerializedName("userType")
    val userType: String = "",
    @SerializedName("employeeName")
    val employeeName: String = "",
    @SerializedName("reportingTo")
    val reportingTo: Int = 0,
    @SerializedName("totalAmount")
    val totalAmount: Int = 0,
    @SerializedName("totalClaims")
    val totalClaims: Int = 0,
    @SerializedName("lastestSubmissionDate")
    val lastestSubmissionDate: String = "",

)