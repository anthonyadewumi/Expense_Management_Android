package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.CarType
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.model.MapingData
import com.google.gson.annotations.SerializedName

class OcrUploadResponse {
    @SerializedName("status")
    var status: Int = 0
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("mapping_data")
    var mapping_data: MapingData? = null
}