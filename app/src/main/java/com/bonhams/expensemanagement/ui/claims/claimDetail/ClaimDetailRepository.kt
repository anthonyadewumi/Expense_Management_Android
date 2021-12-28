package com.bonhams.expensemanagement.ui.claims.claimDetail

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.DeleteClaimRequest
import com.google.gson.JsonObject


class ClaimDetailRepository(private val apiHelper: ApiHelper) {

    suspend fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = apiHelper.deleteClaim(deleteClaimRequest)
    suspend fun sendReminder(claimId: JsonObject) = apiHelper.sendReminder(claimId)
    suspend fun getDetails(claimId: JsonObject,mclaimId: String) = apiHelper.getDetails(claimId,mclaimId)
}