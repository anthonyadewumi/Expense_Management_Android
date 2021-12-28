package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class MyProfileResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    var profileDetail: ProfileDetails? = null
}

class ProfileDetails() {
    val id: String = ""
    val name: String = ""
    val profileImage: String = ""
    val fname: String = ""
    val lname: String = ""
    val email: String = ""
    @SerializedName("contact_no")
    val contactNo: String = ""
    val companyName: String = ""
    val departmentName: String = ""
    val carType: String = ""
    val mileageType: String = ""
    @SerializedName("employId")
    val employID: String = ""
    val approver: String = ""
    val countryCode: String = ""
    @SerializedName("distance_covered")
    val distance_covered: String = ""
}
