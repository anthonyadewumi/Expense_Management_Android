package com.bonhams.expensemanagement.data.model

import com.google.gson.annotations.SerializedName

class Notification {
    var response: Response? = null

    inner class Response {
        var code: String? = null
        var message: String? = null

        @SerializedName("data")
        var dataNotificationList: List<NotificationItem>? = null

        @SerializedName("new_notifi")
        var newNotification = 0
    }
}

class NotificationItem {
    var id: String? = null
    var order_id: String? = null

    @SerializedName("order_incrementid")
    var orederIncrementId: String? = null

    @SerializedName("is_read")
    var isRead: String? = null
    var msg: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null
    var time: String? = null
}
