package com.bonhams.expensemanagement.data.model

data class SplitClaimItem (
    val companyNumber: String = "",
    val companyCode: String = "",
    val department: String = "",
    val expenseType: String = "",
    val totalAmount: String = "",
    val taxcode: String = "",
    val tax: String = ""
)