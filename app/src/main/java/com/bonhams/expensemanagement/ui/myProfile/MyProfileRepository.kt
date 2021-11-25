package com.bonhams.expensemanagement.ui.myProfile

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.google.gson.JsonObject


class MyProfileRepository(private val apiHelper: ApiHelper) {

    suspend fun profileDetail() = apiHelper.profileDetail()
    suspend fun editProfile(data: JsonObject) = apiHelper.editProfile(data)
}