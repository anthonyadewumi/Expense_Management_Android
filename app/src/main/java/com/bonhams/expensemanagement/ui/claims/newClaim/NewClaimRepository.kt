package com.bonhams.expensemanagement.ui.claims.newClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody


class NewClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun uploadImage(newClaimRequest: RequestBody) = apiHelper.uploadImage(newClaimRequest)
    suspend fun uploadClaim(claimImage :List<MultipartBody.Part>) = apiHelper.uploadClaim(claimImage)
}