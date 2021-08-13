package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("db_id")
    var username: String? = null
    var password: String? = null
    var deviceToken: String? = null
    var langCode: String? = null
}