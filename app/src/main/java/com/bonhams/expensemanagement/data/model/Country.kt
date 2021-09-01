package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class Country (
    val id: String = "",
    @SerializedName("country_code")
    val countryCode: String = "",
    @SerializedName("country_name")
    val countryName: String = ""
)