package com.bonhams.expensemanagement.ui.resetPassword

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ResetPasswordRequest

class ResetPasswordRepository(private val apiHelper: ApiHelper) {
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) = apiHelper.resetPassword(resetPasswordRequest)
}