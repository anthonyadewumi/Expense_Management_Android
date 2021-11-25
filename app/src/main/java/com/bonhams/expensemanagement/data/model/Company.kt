package com.bonhams.expensemanagement.data.model

data class Company(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val location: String = "",
    val currency_type_id: Int = 0,
    val dateFormat: String = "",
)
