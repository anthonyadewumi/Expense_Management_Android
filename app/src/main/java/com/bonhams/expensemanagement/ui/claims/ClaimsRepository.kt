package com.bonhams.expensemanagement.ui.claims

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest


class ClaimsRepository(private val apiHelper: ApiHelper) {

    suspend fun getClaimsList(claimsRequest: ClaimsRequest) = apiHelper.claimsList(claimsRequest)
}