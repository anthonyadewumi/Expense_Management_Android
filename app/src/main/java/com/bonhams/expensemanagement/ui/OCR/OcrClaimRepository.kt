package com.bonhams.expensemanagement.ui.OCR

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody


class OcrClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun editClaim(newClaimRequest: JsonObject) = apiHelper.editClaim(newClaimRequest)
    suspend fun uploadOcrImage(newClaimRequest: RequestBody) = apiHelper.uploadOcrImage(newClaimRequest)
    suspend fun uploadClaim(claimImage :List<MultipartBody.Part>) = apiHelper.uploadClaim(claimImage)

}