package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.BatchData
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.model.NotificationData
import com.google.gson.annotations.SerializedName

class BatchListResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("list")
    var batchData: List<BatchData>? = emptyList()
}










