package com.bonhams.expensemanagement.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class LoginResponse() : Parcelable {
    var response: Response? = null

    constructor(parcel: Parcel) : this() {

    }

    inner class Response {
        var code: String? = null
        var token: String? = null
        var message: String? = null

        @SerializedName("userDetails")
        var userDetails: MsgUserDetails? = null

        @SerializedName("data")
        var data: ResponseData? = null

        @SerializedName("userData")
        var userData: UserDetails? = null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginResponse> {
        override fun createFromParcel(parcel: Parcel): LoginResponse {
            return LoginResponse(parcel)
        }

        override fun newArray(size: Int): Array<LoginResponse?> {
            return arrayOfNulls(size)
        }
    }
}

class MsgUserDetails() : Parcelable {
    var mobile_number: String? = null
    var country_code: String? = null
    var dbId: String? = null

    constructor(parcel: Parcel) : this() {
        mobile_number = parcel.readString()
        country_code = parcel.readString()
        dbId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mobile_number)
        parcel.writeString(country_code)
        parcel.writeString(dbId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MsgUserDetails> {
        override fun createFromParcel(parcel: Parcel): MsgUserDetails {
            return MsgUserDetails(parcel)
        }

        override fun newArray(size: Int): Array<MsgUserDetails?> {
            return arrayOfNulls(size)
        }
    }

}

class ResponseData {
    var qouteId = 0
}

