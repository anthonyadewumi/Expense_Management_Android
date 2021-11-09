package com.bonhams.expensemanagement.ui.FinanceManger

import androidx.paging.PagingData
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import kotlinx.coroutines.flow.Flow


interface FinanceRepository {
    suspend fun getClaimsList(claimsRequest: ClaimsRequest) : Flow<PagingData<ToBeAcceptedData>>
}