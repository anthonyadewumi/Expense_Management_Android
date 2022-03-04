package com.bonhams.expensemanagement.ui.main

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.google.gson.JsonObject


class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun logoutUser() = apiHelper.logoutUser()
    suspend fun addFcmKey(json : JsonObject) = apiHelper.addFcmKey(json)
    suspend fun profileDetail() = apiHelper.profileDetail()
}