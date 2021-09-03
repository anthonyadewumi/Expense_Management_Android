package com.bonhams.expensemanagement.ui.myProfile

import com.bonhams.expensemanagement.data.services.ApiHelper


class MyProfileRepository(private val apiHelper: ApiHelper) {

    suspend fun profileDetail() = apiHelper.profileDetail()
}