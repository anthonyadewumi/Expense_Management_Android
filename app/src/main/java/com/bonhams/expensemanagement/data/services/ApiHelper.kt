package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.LoginRequest

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLogin(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

}