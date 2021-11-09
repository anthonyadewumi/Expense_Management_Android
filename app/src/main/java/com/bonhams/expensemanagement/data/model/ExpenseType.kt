package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseType (
    @SerializedName("id")
    val id: String = "",
    val name: String = "",
    val description: String = "",
    @SerializedName("company_id")
    val companyID: String = "",
    @SerializedName("expense_group_id")
    val expenseGroupID: String = "",
    @SerializedName("tax_code_id")
    val taxCodeID: String = "",
    @SerializedName("default_unit_price")
    val defaultUnitPrice: String = "",
    @SerializedName("expense_code_id")
    val expenseCodeID: String = "",
    @SerializedName("activity_code")
    val activityCode: String = "",
    val status: String = ""
)