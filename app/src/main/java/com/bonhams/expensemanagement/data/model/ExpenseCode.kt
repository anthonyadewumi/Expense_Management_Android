package com.bonhams.expensemanagement.data.model

data class ExpenseCode (
    val id: Int = 0,
    val expenseCode: String = "",
    val name: String = "",
    val status: String = "",
    val expense_group_id: String = "",
    val expense_code_id: String = "",
    val tax_code_id: String = "",
)