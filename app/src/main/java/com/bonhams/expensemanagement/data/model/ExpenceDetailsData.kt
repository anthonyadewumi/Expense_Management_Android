package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName


data class ExpenceDetailsData(
    @SerializedName("requestId")
    val requestId: Int = 0,
    @SerializedName("claimTitle")
    val claimTitle: String = "",
    @SerializedName("claimType")
    val claimType: String = "",
    @SerializedName("expenseGroupName")
    val expenseGroupName: String = "",
    @SerializedName("netAmount")
    val netAmount: Int = 0,
    @SerializedName("totalAmount")
    val totalAmount: Int = 0,
    @SerializedName("trip")
    val trip: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("submittedOn")
    val submittedOn: String = "",
    @SerializedName("createdBy")
    val createdBy: Int = 0,
)