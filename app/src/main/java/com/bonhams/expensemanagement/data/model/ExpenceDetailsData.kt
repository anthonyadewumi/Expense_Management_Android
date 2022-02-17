package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ExpenceDetailsData(
    @SerializedName("requestId")
    val requestId: Int = 0,
    @SerializedName("claimTitle")
    val claimTitle: String = "",
    @SerializedName("claimType")
    val claimType: String = "",
    @SerializedName("expenseGroupName")
    val expenseGroupName: String = "",
    @SerializedName("expenseType_mileage")
    val expenseType_mileage: String = "",
    @SerializedName("netAmount")
    val netAmount: Double = 0.0,
    @SerializedName("totalAmount")
    val totalAmount: Double = 0.0,
    @SerializedName("trip")
    val trip: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("submittedOn")
    val submittedOn: String = "",
    @SerializedName("createdBy")
    val createdBy: Int = 0,
    @SerializedName("tax")
    val tax: Double = 0.0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("merchant")
    val merchant: String = "",
    @SerializedName("attachments")    val attachments: String = "",
    @SerializedName("expenseType_expense")
    val expenseType_expense: String = "",
    @SerializedName("from_location")
    val from_location: String = "",
    @SerializedName("to_location")
    val to_location: String = "",
    @SerializedName("miles_claimed")
    val miles_claimed: String = "",
    @SerializedName("carType")
    val carType: String = "",
    @SerializedName("is_round_trip")
    val is_round_trip: Int = 0,
    @SerializedName("date_of_trip")
    val date_of_trip: String = "",
    @SerializedName("currency_type")
    val currency_type: String = "",
    @SerializedName("mileageType")
    val mileageType: String = "",
):Serializable