package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class MapingData (
    @SerializedName("net_amount")
    val net_amount: String = "",
    @SerializedName("receipt_date")
    val receipt_date: String = "",
    @SerializedName("merchant_name")
    val merchant_name: String = "",
    @SerializedName("vat_amount")
    val vat_amount: String = "",
    @SerializedName("vat_rate")
    val vat_rate: String = "",
    @SerializedName("total")
    val total: String = "",
    @SerializedName("tax")
    val tax: String = "",
    @SerializedName("currency")
    val currency: String = "",
)
