package com.bonhams.expensemanagement.ui.claims.claimDetail

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest


class ClaimDetailRepository(private val apiHelper: ApiHelper) {

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
}