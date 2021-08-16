package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class LogoutRequest {
    @SerializedName("mobile_number")
    var contactNummber: String? = null

}
