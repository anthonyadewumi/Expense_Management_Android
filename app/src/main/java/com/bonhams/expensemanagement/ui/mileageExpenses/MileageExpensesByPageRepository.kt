package com.bonhams.expensemanagement.ui.mileageExpenses

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.bonhams.expensemanagement.data.pagingSources.MileageExpensesPagingSource
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.google.gson.JsonObject

class MileageExpensesByPageRepository(val apiHelper: ApiHelper) : MileageExpensesRepository {
    override suspend fun getMileageExpensesList(mileageRequest: MileageExpenseRequest) = Pager(
        PagingConfig(mileageRequest.numberOfItems)
    ) {
        MileageExpensesPagingSource(
            apiHelper = apiHelper,
            mileageRequest = mileageRequest
        )
    }.flow
    suspend fun getClaimedTotal(requestObject: JsonObject) = apiHelper.getClaimedTotal(requestObject)

}