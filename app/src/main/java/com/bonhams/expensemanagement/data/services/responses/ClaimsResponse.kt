package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.google.gson.annotations.SerializedName

class ClaimsResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    var claimsList: List<ClaimDetail>? = null
}