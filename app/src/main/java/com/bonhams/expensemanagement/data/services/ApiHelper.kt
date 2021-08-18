package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.*

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLogin(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun forgotPassword(forgotPassRequest: ForgotPasswordRequest) = apiService.forgotPassword(forgotPassRequest)

    suspend fun claimsList(claimListRequest: ClaimsRequest) = apiService.claimsList(claimListRequest)

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiService.createNewClaim(newClaimRequest)

    suspend fun mileageExpensesList(mileageExpenseRequest: MileageExpenseRequest) = apiService.mileageList(mileageExpenseRequest)

    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiService.createNewMileageClaim(mileageClaimRequest)

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = apiService.changePassword(changePasswordRequest)

    suspend fun logoutUser() = apiService.logoutUser()
}