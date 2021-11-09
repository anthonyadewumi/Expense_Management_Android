package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class ForgotPasswordRequest {
    @SerializedName("email")
    var email: String? = null
    @SerializedName("role")
    var role: String? = null
}
