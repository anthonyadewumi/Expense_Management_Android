package com.bonhams.expensemanagement.ui.claims.claimDetail

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.DeleteClaimRequest


class ClaimDetailRepository(private val apiHelper: ApiHelper) {

    suspend fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = apiHelper.deleteClaim(deleteClaimRequest)
}