package com.bonhams.expensemanagement.ui.rmExpence

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ExpenceRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun getRequestExpences(mileageExpenseRequest: JsonObject) = apiHelper.getRequestExpences(mileageExpenseRequest)
    suspend fun getRequestExpencesDetails(mileageExpenseRequest: JsonObject) = apiHelper.getRequestExpencesDetails(mileageExpenseRequest)
    suspend fun acceptReject(data: JsonObject) = apiHelper.acceptReject(data)
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun uploadImage(newClaimRequest: RequestBody) = apiHelper.uploadImage(newClaimRequest)
    suspend fun getDetails(claimId: JsonObject,mclaimId: String) = apiHelper.getDetails(claimId,mclaimId)
    suspend fun getMielageDetails(claimId: JsonObject,mclaimId: String) = apiHelper.getMielageDetails(claimId,mclaimId)

}