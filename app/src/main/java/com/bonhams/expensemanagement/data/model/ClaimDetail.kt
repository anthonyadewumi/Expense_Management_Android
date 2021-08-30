package com.bonhams.expensemanagement.data.model


data class ClaimDetail(
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    val id: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    val group: Currency? = null,
    val type: Currency? = null,
    val companyName: String = "",
    val department: String = "",
    val currency: Currency? = null,
    val tax: String = "",
    val netAmount: String = "",
    val merchant: String = "",
    val attachments: String = ""
//    val attachments: List<String>? = null
)