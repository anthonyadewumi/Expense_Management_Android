package com.bonhams.expensemanagement.data.services.responses

import com.google.gson.annotations.SerializedName

class DistanceMatrixResponse {
    @SerializedName("destination_addresses")
    val destination_addresses: List<String> = emptyList()
    @SerializedName("origin_addresses")
    val origin_addresses: List<String> = emptyList()
    @SerializedName("rows")
    val rows: List<Rows> = emptyList()
    @SerializedName("status")
    var status: String? = ""
}

class Rows() {
    @SerializedName("elements")
    val elements: List<Elements> = emptyList()


}
class Elements() {
    @SerializedName("distance")
    var distance: Distance? = null
    @SerializedName("duration")
    var duration: Duration? = null
}
class Distance() {
    val text: String = ""
    val value: Int = 0
}
class Duration() {
    val text: String = ""
    val value: Int = 0
}