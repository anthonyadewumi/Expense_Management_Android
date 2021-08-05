package com.bonhams.expensemanagement.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Login() : Parcelable {
    @SerializedName("response")
    var loginbody: LoginInner? = null

    constructor(parcel: Parcel) : this() {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Login> {
        override fun createFromParcel(parcel: Parcel): Login {
            return Login(parcel)
        }

        override fun newArray(size: Int): Array<Login?> {
            return arrayOfNulls(size)
        }
    }
}

class LoginInner() : Parcelable {
    var code = 0
    var token: String? = null
    var message: String? = null

    @SerializedName("userData")
    var userDetails: UserDetails? = null
    var rememberMe: Boolean? = null

    constructor(parcel: Parcel) : this() {
        code = parcel.readInt()
        token = parcel.readString()
        message = parcel.readString()
        userDetails = parcel.readParcelable(UserDetails::class.java.classLoader)
        rememberMe = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(code)
        parcel.writeString(token)
        parcel.writeString(message)
        parcel.writeParcelable(userDetails, flags)
        parcel.writeValue(rememberMe)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginInner> {
        override fun createFromParcel(parcel: Parcel): LoginInner {
            return LoginInner(parcel)
        }

        override fun newArray(size: Int): Array<LoginInner?> {
            return arrayOfNulls(size)
        }
    }

}

class ExtensionAttributes() : Parcelable {
    var is_subscribed = false

    constructor(parcel: Parcel) : this() {
        is_subscribed = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (is_subscribed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExtensionAttributes> {
        override fun createFromParcel(parcel: Parcel): ExtensionAttributes {
            return ExtensionAttributes(parcel)
        }

        override fun newArray(size: Int): Array<ExtensionAttributes?> {
            return arrayOfNulls(size)
        }
    }
}

class UserDetails() : Parcelable {
    @SerializedName("db_id")
    var dbId: String? = null

    @SerializedName("user_id")
    var userId: String? = null
    var name: String? = null

    @SerializedName("country_code")
    var countryCode: String? = null

    @SerializedName("contact_no")
    var contactNo: String? = null
    var email: String? = null

    @SerializedName("vehicle_type")
    var vehicleType: String? = null

    @SerializedName("vehicle_no")
    var vehicleNo: String? = null
    var online: String? = null
    var rememberMe: Boolean = false

    constructor(parcel: Parcel) : this() {
        dbId = parcel.readString()
        userId = parcel.readString()
        name = parcel.readString()
        countryCode = parcel.readString()
        contactNo = parcel.readString()
        email = parcel.readString()
        vehicleType = parcel.readString()
        vehicleNo = parcel.readString()
        online = parcel.readString()
        rememberMe = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dbId)
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(countryCode)
        parcel.writeString(contactNo)
        parcel.writeString(email)
        parcel.writeString(vehicleType)
        parcel.writeString(vehicleNo)
        parcel.writeString(online)
        parcel.writeByte(if (rememberMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetails> {
        override fun createFromParcel(parcel: Parcel): UserDetails {
            return UserDetails(parcel)
        }

        override fun newArray(size: Int): Array<UserDetails?> {
            return arrayOfNulls(size)
        }
    }
}

