package com.bonhams.expensemanagement.ui.claims

import androidx.paging.PagingData
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import kotlinx.coroutines.flow.Flow


interface ClaimsRepository {
    suspend fun getClaimsList(claimsRequest: ClaimsRequest) : Flow<PagingData<ClaimDetail>>
}