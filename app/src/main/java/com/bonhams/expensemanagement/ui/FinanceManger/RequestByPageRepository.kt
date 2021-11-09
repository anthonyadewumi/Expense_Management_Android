package com.bonhams.expensemanagement.ui.FinanceManger

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.bonhams.expensemanagement.data.pagingSources.ClaimsPagingSource
import com.bonhams.expensemanagement.data.pagingSources.FinancePagingSource
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest

class RequestByPageRepository(val apiHelper: ApiHelper) : FinanceRepository {
    override suspend fun getClaimsList(claimsRequest: ClaimsRequest) = Pager(
        PagingConfig(claimsRequest.numberOfItems)
    ) {
        FinancePagingSource(
            apiHelper = apiHelper,
            claimsRequest = claimsRequest
        )
    }.flow
}