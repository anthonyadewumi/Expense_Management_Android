package com.bonhams.expensemanagement.ui.mileageExpenses

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest


class MileageExpensesRepository(private val apiHelper: ApiHelper) {

    suspend fun getMileageExpensesList(mileageRequest: MileageExpenseRequest) = apiHelper.mileageExpensesList(mileageRequest)
}