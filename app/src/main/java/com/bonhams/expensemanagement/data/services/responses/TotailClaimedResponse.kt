package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.*
import com.google.gson.annotations.SerializedName

class TotailClaimedResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    val data: List<TotalClaimedData> = emptyList()
}
class TotalClaimedData{
    @SerializedName("user_id")
    var user_id: String? = ""
    @SerializedName("name")
    var name: String? = ""
    @SerializedName("total_amount")
    var total_amount: Double? = 0.0
    @SerializedName("currency_type")
    var currency_type: String? = ""
    @SerializedName("miles_claimed")
    var miles_claimed: Double? = 0.0
    @SerializedName("mileage_type")
    var mileage_type: String? = ""


}