package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.*
import com.google.gson.annotations.SerializedName

class AcceptRequestResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    val tobeaccepteddata: List<ToBeAcceptedData> = emptyList()

}