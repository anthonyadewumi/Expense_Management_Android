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
    @SerializedName("batch_allotted")
    val batch_allotted: Int = 0,
    @SerializedName("e_claims")
    val e_claims: Int = 0,
    @SerializedName("m_claims")
    val m_claims: Int = 0,
    @SerializedName("currency_symbol")
    val currency_type: String = "",
    @SerializedName("company_code")
    val company_code: String = "",
    @SerializedName("ledger_id")
    val ledger_id: String = "",
    @SerializedName("lastestSubmissionDate")
    val lastestSubmissionDate: String = "",

)