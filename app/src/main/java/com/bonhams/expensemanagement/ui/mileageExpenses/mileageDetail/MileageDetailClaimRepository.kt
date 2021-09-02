package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest


class MileageDetailClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiHelper.createNewMileageClaim(mileageClaimRequest)
}