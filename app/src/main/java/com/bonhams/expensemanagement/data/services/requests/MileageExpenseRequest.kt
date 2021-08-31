package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class MileageExpenseRequest {
    @SerializedName("page")
    var page: Int = 1
    @SerializedName("numberOfItems")
    var numberOfItems: Int = 10
    @SerializedName("searchKey")
    var searchKey: String? = ""
    @SerializedName("from")
    var fromDate: String? = null
    @SerializedName("to")
    var toDate: String? = null
    @SerializedName("status")
    var status: String? = null
}