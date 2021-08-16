package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class CommonResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
}