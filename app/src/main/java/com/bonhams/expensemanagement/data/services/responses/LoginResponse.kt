package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("userDetails")
    var userDetails: UserDetails? = null
}

class UserDetails() {
    var name: String? = null
    var id: String? = null
    var profileImage: String? = null
    var fname: String? = null
    var lname: String? = null
    @SerializedName("userId")
    var email: String? = null
    var countryCode: String? = null
    var companyName: String? = null
    var departmentName: String? = null
    var carType: String? = null
    var mileageType: String? = null
    var approver: String? = null
    var employId: String? = null
    var token: String? = null
    var refresh_token: String? = null
}
