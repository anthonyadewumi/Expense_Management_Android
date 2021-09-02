package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.MileageDetail
import com.google.gson.annotations.SerializedName

class MileageListResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("list")
    var mileageList: List<MileageDetail>? = emptyList()
}










