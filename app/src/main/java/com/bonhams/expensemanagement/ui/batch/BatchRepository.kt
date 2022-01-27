package com.bonhams.expensemanagement.ui.batch

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.google.gson.JsonObject


class BatchRepository(private val apiHelper: ApiHelper) {

 suspend fun getBatchData(request: JsonObject) = apiHelper.getBatchData(request)
}