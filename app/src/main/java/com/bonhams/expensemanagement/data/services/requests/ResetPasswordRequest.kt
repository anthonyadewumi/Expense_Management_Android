package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class ResetPasswordRequest {
    @SerializedName("password")
    var password: String? = null
}
