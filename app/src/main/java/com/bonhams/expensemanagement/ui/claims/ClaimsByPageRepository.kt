package com.bonhams.expensemanagement.ui.claims

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.bonhams.expensemanagement.data.pagingSources.ClaimsPagingSource
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.google.gson.JsonObject

class ClaimsByPageRepository(val apiHelper: ApiHelper) : ClaimsRepository {
    override suspend fun getClaimsList(claimsRequest: ClaimsRequest) = Pager(
        PagingConfig(claimsRequest.numberOfItems)
    ) {
        ClaimsPagingSource(
            apiHelper = apiHelper,
            claimsRequest = claimsRequest
        )
    }.flow

    suspend fun getClaimedTotal(requestObject: JsonObject) = apiHelper.getClaimedTotal(requestObject)

}