package com.bonhams.expensemanagement.ui.forgotPassword

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ForgotPasswordRequest

class ForgotPasswordRepository(private val apiHelper: ApiHelper) {
    suspend fun forgotPassword(forgotPassRequest: ForgotPasswordRequest) = apiHelper.forgotPassword(forgotPassRequest)
}