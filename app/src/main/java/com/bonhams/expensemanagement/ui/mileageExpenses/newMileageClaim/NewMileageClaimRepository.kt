package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.GoogleApiHelper
import com.bonhams.expensemanagement.data.services.GoogleApiService
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import retrofit2.http.QueryMap


class NewMileageClaimRepository(private val apiHelper: ApiHelper,private val googleApiHelper: GoogleApiHelper) {

    suspend fun dropdownData() = apiHelper.dropdownData()
    suspend fun getdistance(@QueryMap parameters: MutableMap<String, String>) = googleApiHelper.getDistanceInfo(parameters)
    suspend fun createNewMileageClaim(mileageClaimRequest: NewMileageClaimRequest) = apiHelper.createNewMileageClaim(mileageClaimRequest)
}