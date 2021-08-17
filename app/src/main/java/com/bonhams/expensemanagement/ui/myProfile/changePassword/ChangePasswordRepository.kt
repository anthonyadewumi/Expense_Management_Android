package com.bonhams.expensemanagement.ui.myProfile.changePassword

import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ChangePasswordRequest


class ChangePasswordRepository(private val apiHelper: ApiHelper) {

    suspend fun getChangePassword(changePasswordRequest: ChangePasswordRequest) = apiHelper.changePassword(changePasswordRequest)
}