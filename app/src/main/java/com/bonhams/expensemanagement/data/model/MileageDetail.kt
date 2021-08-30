package com.bonhams.expensemanagement.data.model

data class MileageDetail (
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    val id: String = "",
    val reportMStatus: String = "",
    val financeMStatus: String = "",
    val comapnyname: String = "",
    val group: Currency? = null,
    val type: String = "",
    val companyname: String = "",
    val department: String = "",
    val currency: String = "", //: Currency? = null,
    val tax: String? = null,
    val netAmount: String? = null,
    val merchant: String = "",
    val attachments: List<String>? = null,
    val trip: Trip? = null,
    val to: From? = null,
    val from: From? = null,
    val distance: String = "",
    val claimMileage: String = "",
    val parking: String = "",
    val petrolAmount: String = "",
    val carType: String = ""
)