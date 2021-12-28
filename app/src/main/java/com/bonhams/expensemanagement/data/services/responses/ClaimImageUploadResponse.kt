package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.CarType
import com.google.gson.annotations.SerializedName

class ClaimImageUploadResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("images")
    val images: List<String> = emptyList()
}