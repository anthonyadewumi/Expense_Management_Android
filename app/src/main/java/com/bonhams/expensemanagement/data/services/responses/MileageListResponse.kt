package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class MileageListResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("data")
    var mileageList: List<MileageDetail>? = null
}

class MileageDetail {
    val title: String = ""
    val description: String = ""
    val totalAmount: String = ""
    val id: String = ""
    val reportMStatus: String = ""
    val financeMStatus: String = ""
    val comapnyname: String = ""
    val group: Currency? = null
    val type: String = ""
    val companyname: String = ""
    val department: String = ""
    val currency: Currency? = null
    val tax: String? = null
    val netAmount: String? = null
    val merchant: String = ""
    val attachments: List<String>? = null
    val trip: Trip? = null
    val to: From? = null
    val from: From? = null
    val distance: String = ""
    val claimMileage: String = ""
    val parking: String = ""
    val petrolAmount: String = ""
    val carType: CarType? = null
}

class CarType {
    val id: String = ""
    val name: String = ""
    val mileageRate: String = ""
}

class Currency {
    val id: String = ""
    val name: String = ""
}

class From {
    val address: String = ""
    val lat: String = ""
    val long: String = ""
}

class Trip {
    val date: String = ""
    val isRoundTrip: Long = 0
}

