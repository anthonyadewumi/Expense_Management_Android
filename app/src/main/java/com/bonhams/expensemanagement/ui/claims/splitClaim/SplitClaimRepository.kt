package com.bonhams.expensemanagement.ui.claims.splitClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest


class SplitClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
}