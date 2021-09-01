package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseType (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    @SerializedName("company_id")
    val companyID: String = "",
    @SerializedName("expense_group_id")
    val expenseGroupID: String = "",
    @SerializedName("tax_id")
    val taxID: String = "",
    @SerializedName("default_unit_price")
    val defaultUnitPrice: String = "",
    @SerializedName("expense_code")
    val expenseCode: String = "",
    @SerializedName("activity_code")
    val activityCode: String = "",
)