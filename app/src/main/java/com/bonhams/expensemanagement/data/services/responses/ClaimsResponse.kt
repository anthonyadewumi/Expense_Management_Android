package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class ClaimsResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    var claimsList: List<MileageDetail>? = null
}

class ClaimDetail() {
    val title: String = ""
    val description: String = ""
    val totalAmount: String = ""
    val id: String = ""
    val reportingMStatus: String = ""
    val financeMStatus: String = ""
    val group: Currency? = null
    val type: Currency? = null
    val companyName: String = ""
    val department: String = ""
    val currency: Currency? = null
    val tax: String = ""
    val netAmount: String = ""
    val merchant: String = ""
    val attachments: List<Attachment>? = null
}

class Attachment {
    val url1: String = ""
    val url2: String = ""
}