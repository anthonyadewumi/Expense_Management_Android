package com.bonhams.expensemanagement.ui.claims.splitClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.google.gson.JsonObject
import okhttp3.RequestBody


class SplitClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
    suspend fun uploadImage(newClaimRequest: RequestBody) = apiHelper.uploadImage(newClaimRequest)
    suspend fun updateSplit(jsonobject: JsonObject) = apiHelper.updateSplit(jsonobject)
    suspend fun editClaim(jsonobject: JsonObject) = apiHelper.editClaim(jsonobject)

}