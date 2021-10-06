package com.bonhams.expensemanagement.data.model

data class Tax (
    val id: Int = 0,
    val tax_code: String = "",
    val tax_type: String = "",
    val company_id: Int = 0,
    val expense_code: String = "",
    val description: String = "",
    val status: String = ""
)