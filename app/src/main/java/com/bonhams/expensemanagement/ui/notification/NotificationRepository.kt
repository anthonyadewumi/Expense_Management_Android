package com.bonhams.expensemanagement.ui.notification

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.google.gson.JsonObject


class NotificationRepository(private val apiHelper: ApiHelper) {

 suspend fun getnoticationData(request: JsonObject) = apiHelper.getnoticationData(request)
}