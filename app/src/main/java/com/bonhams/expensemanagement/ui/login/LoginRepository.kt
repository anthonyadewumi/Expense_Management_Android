package com.bonhams.expensemanagement.ui.login

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.LoginRequest

class LoginRepository(private val apiHelper: ApiHelper) {
    suspend fun getLogin(loginRequest: LoginRequest) = apiHelper.getLogin(loginRequest)
}