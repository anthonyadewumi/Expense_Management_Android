package com.bonhams.expensemanagement.data.model

data class SplitClaimDetail (
    val companyNumber: String = "",
    val department: String = "",
    val expenseType: String = "",
    val totalAmount: String = "",
    val tax: Double = 0.0,
    val taxCode: Int = 0,
    var auction: String? = "",
    var expenseCode: String? = ""
)