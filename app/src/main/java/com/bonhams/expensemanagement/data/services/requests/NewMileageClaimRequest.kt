package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class NewMileageClaimRequest {

    var companyName: String? = ""
    var mileageType: String? = ""
    var department: String? = ""
    var dateSubmitted: String? = ""
    var expenseType:  String? = ""
    @SerializedName("MerchantName")
    var merchantName: String? = ""
    var tripDate: String? = ""
    var tripFrom: String? = ""
    var tripTo: String? = ""
    var distance: String? = ""
    var carType: String? = ""
    var claimedMiles: String? = ""
    var roundTrip: String? = ""
    var currency:  String? = ""
    var petrolAmount:  String? = ""
    var parkAmount:  String? = ""
    var totalAmount:  String? = ""
    var tax: String? = ""
    var netAmount: String? = ""
    var description: String? = ""
    var attachments: List<String> = emptyList()
}