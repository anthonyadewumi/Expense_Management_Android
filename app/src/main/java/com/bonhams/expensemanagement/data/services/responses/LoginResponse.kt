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
    val id: String = ""
    val name: String = ""
    val profileImage: String = ""
    val fname: String = ""
    val lname: String = ""
    val email: String = ""
    @SerializedName("contact_no")
    val contactNo: String = ""
    @SerializedName("user_type")
    val userType: String = ""
    val companyName: String = ""
    val departmentName: String = ""
    val carType: String = ""
    val mileageType: String = ""
    @SerializedName("employId")
    val employID: String = ""
    val isReset: Long = 0
    val approver: String = ""
    val countryCode: String = ""
    val token: String = ""
    @SerializedName("refresh_token")
    val refreshToken: String = ""
}
