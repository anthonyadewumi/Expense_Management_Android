package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName


data class ClaimDetail(
    val id: String = "",
    val createdBy: String = "",
    @SerializedName("userTypeId")
    val userTypeID: String = "",
    val userType: String = "",
    @SerializedName("employ_id")
    val employID: String = "",
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    @SerializedName("created_on")
    val createdOn: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    @SerializedName("expense_group_id")
    val expenseGroupID: String = "",
    @SerializedName("expense_group_name")
    val expenseGroupName: String = "",
    @SerializedName("expense_type_id")
    val expenseTypeID: String = "",
    @SerializedName("expense_type_name")
    val expenseTypeName: String = "",
    val companyName: String = "",
    val department: String = "",
    @SerializedName("currency_type_id")
    val currencyTypeID: String = "",
    @SerializedName("currency_type_name")
    val currencyTypeName: String = "",
    val tax: String = "",
    val netAmount: String = "",
    val merchant: String = "",
    val attachments: String = "",
)