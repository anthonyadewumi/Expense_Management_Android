package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

data class NotificationData (
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("date_time")
    val date_time: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("isRead")
    val isRead: Int = 0
)