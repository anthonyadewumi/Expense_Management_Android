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
    val reportingTo: String = "",
    @SerializedName("totalAmount")
    val totalAmount: Double = 0.0,
    @SerializedName("totalClaims")
    val totalClaims: Int = 0,
    @SerializedName("lastestSubmissionDate")
    val lastestSubmissionDate: String = "",

)