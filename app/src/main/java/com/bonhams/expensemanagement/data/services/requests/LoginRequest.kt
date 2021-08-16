package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("userId")
    var username: String? = null
    @SerializedName("password")
    var password: String? = null
//    var deviceToken: String? = null
}