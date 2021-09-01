package com.bonhams.expensemanagement.ui.claims.newClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest


class NewClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewClaim(newClaimRequest: NewClaimRequest) = apiHelper.createNewClaim(newClaimRequest)
}