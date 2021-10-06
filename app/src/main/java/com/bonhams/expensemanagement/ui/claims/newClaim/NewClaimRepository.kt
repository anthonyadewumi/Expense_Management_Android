package com.bonhams.expensemanagement.ui.claims.newClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import okhttp3.MultipartBody


class NewClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun uploadImage(claimImage :List<MultipartBody.Part>) = apiHelper.uploadImage(claimImage)
}