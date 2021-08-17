package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class ChangePasswordRequest {
    @SerializedName("password")
    var password: String? = ""
    @SerializedName("newPassword")
    var newPassword: String? = ""
}