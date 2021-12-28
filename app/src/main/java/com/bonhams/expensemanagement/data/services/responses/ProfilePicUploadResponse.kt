package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class ProfilePicUploadResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("image_url")
    var image_url: String? = ""
}