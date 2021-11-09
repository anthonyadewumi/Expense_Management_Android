package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.DeleteClaimRequest
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.google.gson.JsonObject


class MileageDetailClaimRepository(private val apiHelper: ApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiHelper.createNewMileageClaim(mileageClaimRequest)
    suspend fun deleteClaim(deleteClaimRequest: DeleteClaimRequest) = apiHelper.deleteClaim(deleteClaimRequest)
    suspend fun sendReminder(claimId: JsonObject) = apiHelper.sendReminder(claimId)

}