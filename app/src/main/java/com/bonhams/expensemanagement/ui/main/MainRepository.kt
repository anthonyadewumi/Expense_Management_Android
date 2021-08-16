package com.bonhams.expensemanagement.ui.main

import com.bonhams.expensemanagement.data.services.ApiHelper


class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun logoutUser() = apiHelper.logoutUser()
}