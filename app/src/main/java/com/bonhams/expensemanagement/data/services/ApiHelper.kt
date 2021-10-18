package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLogin(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun forgotPassword(forgotPassRequest: ForgotPasswordRequest) = apiService.forgotPassword(forgotPassRequest)

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) = apiService.resetPassword(resetPasswordRequest)

    suspend fun claimsList(claimListRequest: ClaimsRequest) = apiService.claimsList(claimListRequest)

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiService.createNewClaim(newClaimRequest)

    suspend fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = apiService.deleteClaim(deleteClaimRequest)
    suspend fun uploadImage(claimImage :List<MultipartBody.Part>) = apiService.uploadImage(claimImage)

    suspend fun mileageExpensesList(mileageExpenseRequest: MileageExpenseRequest) = apiService.mileageList(mileageExpenseRequest)

    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiService.createNewMileageClaim(mileageClaimRequest)

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = apiService.changePassword(changePasswordRequest)

    suspend fun dropdownData() = apiService.dropdownData()
    suspend fun getRequestExpences(mileageExpenseRequest: JsonObject) = apiService.getRequestExpences(mileageExpenseRequest)
    suspend fun getRequestExpencesDetails(mileageExpenseRequest: JsonObject) = apiService.getRequestExpencesDetails(mileageExpenseRequest)

    suspend fun logoutUser() = apiService.logoutUser()

    suspend fun profileDetail() = apiService.profileDetail()
}