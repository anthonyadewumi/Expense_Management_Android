package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLogin(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun forgotPassword(forgotPassRequest: ForgotPasswordRequest) = apiService.forgotPassword(forgotPassRequest)

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) = apiService.resetPassword(resetPasswordRequest)

    suspend fun claimsList(claimListRequest: ClaimsRequest) = apiService.claimsList(claimListRequest)

    suspend fun financeRequestList(claimListRequest: ClaimsRequest) = apiService.financeRequestList(claimListRequest)

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiService.createNewClaim(newClaimRequest)
    suspend fun editClaim(newClaimRequest: JsonObject) = apiService.editClaim(newClaimRequest)
    suspend fun getnoticationData(request: JsonObject) = apiService.getnoticationData(request)
    suspend fun getBatchData(request: JsonObject) = apiService.getBatchData(request)
    suspend fun deleteBatch(request: JsonObject) = apiService.deleteBatch(request)
    suspend fun submitBatch(request: JsonObject) = apiService.submitBatch(request)

    suspend fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = apiService.deleteClaim(deleteClaimRequest)
    suspend fun sendReminder(claimId: JsonObject) = apiService.sendReminder(claimId)
    suspend fun getDetails(claimId: JsonObject,mclaimId: String) = apiService.getDetails(claimId)
    suspend fun getMielageDetails(claimId: JsonObject,mclaimId: String) = apiService.getMielageDetails(claimId)
    suspend fun updateSplit(jsonobject: JsonObject) = apiService.updateSplit(jsonobject)
    suspend fun uploadImage(newClaimRequest: RequestBody) = apiService.uploadImage(newClaimRequest)
    suspend fun uploadOcrImage(newClaimRequest: RequestBody) = apiService.uploadOcrImage("http://ocrdev.iworklab.com/upload",newClaimRequest)
    suspend fun uploadProfileImage(claimImage :List<MultipartBody.Part>) = apiService.uploadProfileImage(claimImage)

    suspend fun uploadClaim(claimImage :List<MultipartBody.Part>) = apiService.uploadClaim(claimImage)

    suspend fun mileageExpensesList(mileageExpenseRequest: MileageExpenseRequest) = apiService.mileageList(mileageExpenseRequest)

    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiService.createNewMileageClaim(mileageClaimRequest)
    suspend fun editNewMileageClaim(mileageClaimRequest: EditMileageClaimRequest) = apiService.editNewMileageClaim(mileageClaimRequest)

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = apiService.changePassword(changePasswordRequest)

    suspend fun dropdownData() = apiService.dropdownData()
    suspend fun getClaimedTotal(requestObject: JsonObject) = apiService.getClaimedTotal(requestObject)
    suspend fun getRequestExpences(mileageExpenseRequest: JsonObject) = apiService.getRequestExpences(mileageExpenseRequest)
    suspend fun getRequestExpencesDetails(mileageExpenseRequest: JsonObject) = apiService.getRequestExpencesDetails(mileageExpenseRequest)
    suspend fun acceptReject(data: JsonObject) = apiService.acceptReject(data)
    suspend fun acceptRejectBatch(data: JsonObject) = apiService.acceptRejectBatch(data)

    suspend fun logoutUser() = apiService.logoutUser()
    suspend fun addFcmKey(requestObject: JsonObject) = apiService.addFcmKey(requestObject)

    suspend fun profileDetail() = apiService.profileDetail()
    suspend fun editProfile(data: JsonObject) = apiService.editProfile(data)
}