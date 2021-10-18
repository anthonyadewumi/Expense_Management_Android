package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.*
import com.google.gson.annotations.SerializedName

class ExpenceDetailsResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    val expenceDetailsData: List<ExpenceDetailsData> = emptyList()

}