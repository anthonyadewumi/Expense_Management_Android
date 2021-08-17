package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class NotificationsRequest {
    @SerializedName("page")
    var page: Int = 1
    @SerializedName("numberOfItems")
    var numberOfItems: Int = 10
}