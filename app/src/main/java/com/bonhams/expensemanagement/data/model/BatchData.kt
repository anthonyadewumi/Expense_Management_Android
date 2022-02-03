package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class BatchData (
    @SerializedName("employeeId")
    val employeeId: String = "",
    @SerializedName("ledger_id")
    val ledger_id: String = "",
    @SerializedName("totalClaims")
    val totalClaims: String = "",
    @SerializedName("employeeName")
    val employeeName: String = "",
    @SerializedName("lastestSubmissionDate")
    val lastestSubmissionDate: String = "",
    @SerializedName("currency_type")
    val currency_type: String = "",
    @SerializedName("currency_symbol")
    val currency_symbol: String = "",
    @SerializedName("totalAmount")
    val totalAmount: Double = 0.0 ,
    @SerializedName("batch_allotted")
    val batch_allotted: Int = 0
)
