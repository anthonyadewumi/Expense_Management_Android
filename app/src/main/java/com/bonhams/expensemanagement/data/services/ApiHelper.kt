package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.ForgotPasswordRequest
import com.bonhams.expensemanagement.data.services.requests.LoginRequest

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLogin(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun forgotPassword(forgotPassRequest: ForgotPasswordRequest) = apiService.forgotPassword(forgotPassRequest)

    suspend fun mileageList(loginRequest: LoginRequest) = apiService.mileageList(loginRequest)

    suspend fun logoutUser() = apiService.logoutUser()
}