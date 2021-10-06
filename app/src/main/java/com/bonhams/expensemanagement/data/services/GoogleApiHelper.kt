package com.bonhams.expensemanagement.data.services

import com.bonhams.expensemanagement.data.services.requests.*
import retrofit2.http.QueryMap

class GoogleApiHelper(private val googleapiService: GoogleApiService) {

    suspend fun getDistanceInfo(@QueryMap parameters: MutableMap<String, String>) = googleapiService.getDistanceInfo(parameters)

   }