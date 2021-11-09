package com.bonhams.expensemanagement.ui.rmExpence

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody


class ExpenceRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun getRequestExpences(mileageExpenseRequest: JsonObject) = apiHelper.getRequestExpences(mileageExpenseRequest)
    suspend fun getRequestExpencesDetails(mileageExpenseRequest: JsonObject) = apiHelper.getRequestExpencesDetails(mileageExpenseRequest)
    suspend fun acceptReject(data: JsonObject) = apiHelper.acceptReject(data)
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun uploadImage(claimImage :List<MultipartBody.Part>) = apiHelper.uploadImage(claimImage)
}