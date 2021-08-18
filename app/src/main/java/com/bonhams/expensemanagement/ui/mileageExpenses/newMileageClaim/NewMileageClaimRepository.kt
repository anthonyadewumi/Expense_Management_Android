package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest


class NewMileageClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiHelper.createNewMileageClaim(mileageClaimRequest)
}