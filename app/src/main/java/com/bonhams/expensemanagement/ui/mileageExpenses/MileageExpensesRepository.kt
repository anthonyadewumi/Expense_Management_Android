package com.bonhams.expensemanagement.ui.mileageExpenses

import androidx.paging.PagingData
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import kotlinx.coroutines.flow.Flow


interface MileageExpensesRepository {
    suspend fun getMileageExpensesList(mileageRequest: MileageExpenseRequest): Flow<PagingData<MileageDetail>>
}